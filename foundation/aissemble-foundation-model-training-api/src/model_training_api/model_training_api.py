###
# #%L
# AIOps Docker Baseline::Model Training API::Service
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from fastapi import FastAPI, Request, HTTPException

from typing import Union
from kubernetes import client, config
from kubernetes.client.rest import ApiException

from krausening.properties import PropertyManager

import uuid
import json
import os
import re

NAMESPACE_PATH = "/var/run/secrets/kubernetes.io/serviceaccount/namespace"
SERVICE_ACCOUNT_NAME = "model-training-api"
MAX_JOB_NAME_LENGTH = 63
# We use this to allow entire uuid to fit in job name to avoid collisions:
TRIMMED_JOB_PREFIX_LENGTH = MAX_JOB_NAME_LENGTH - (len(str(uuid.uuid4())) + 1)

config.load_incluster_config()
batch_v1 = client.BatchV1Api()
core_v1 = client.CoreV1Api()
apps_v1 = client.AppsV1Api()

properties = PropertyManager.get_instance().get_properties(
    "training-api-config.properties"
)
project_name = properties["project_name"]
image_tag = properties["image_tag"]
docker_registry = properties["docker_registry"]
image_prefix = properties["image_prefix"]
image_pull_policy = properties["image_pull_policy"]
namespace = open(NAMESPACE_PATH).read()


app = FastAPI()


@app.get("/")
def root():
    return "Model Training API service is running"


def get_dashed_from_camel_cased(camel_cased):
    # the following regex converts CamelCased string to lower-cased dashed string
    dashed = re.sub("([A-Z]+)", r"-\1", camel_cased).lower()
    if camel_cased[0].isupper():
        return dashed[1:]
    else:
        return dashed


def get_training_image(pipeline_step):
    dashed_step_name = get_dashed_from_camel_cased(pipeline_step)
    training_image = f"{docker_registry}{image_prefix}{project_name}-{dashed_step_name}-docker:{image_tag}"
    return training_image


@app.post("/training-jobs")
async def trigger_training(pipeline_step: str, training_params: Request):
    """Trigger model training Kubernetes job for specific pipeline step, with provided training parameters
    Args:
        pipeline_step: training pipeline step name
        training_params: POST request body containing any parameters required for model training
    Returns:
        model training job name
    Raises:
        HTTPException: if job could not be created
    """

    try:
        # Read in user-provided training parameters, will be passed as env vars
        training_params = await training_params.json()

        training_env_vars = []
        for k, v in training_params.items():
            training_env_vars.append(client.V1EnvVar(name=k, value=v))

        pipeline_step_dashed = get_dashed_from_camel_cased(pipeline_step)

        training_image = get_training_image(pipeline_step_dashed)

        job_name = (
            f"model-training-{pipeline_step_dashed}"[:TRIMMED_JOB_PREFIX_LENGTH]
            + f"-{str(uuid.uuid4())}"
        )

        model_training_container = client.V1Container(
            name="model-training",
            image=training_image,
            image_pull_policy=image_pull_policy,
            env=training_env_vars,
        )

        pod_spec = client.V1PodSpec(
            service_account_name=SERVICE_ACCOUNT_NAME,
            restart_policy="Never",
            containers=[model_training_container],
        )

        template = client.V1PodTemplateSpec(
            metadata=client.V1ObjectMeta(name=job_name), spec=pod_spec
        )

        spec = client.V1JobSpec(backoff_limit=0, template=template)
        job = client.V1Job(
            api_version="batch/v1",
            kind="Job",
            metadata=client.V1ObjectMeta(
                name=job_name,
            ),
            spec=spec,
        )

        batch_v1.create_namespaced_job(body=job, namespace=namespace)

        return job_name

    except ApiException as e:
        print(e, flush=True)
        raise HTTPException(status_code=500, detail=str(e))


@app.get("/training-jobs/{training_job_name}")
def get_job_logs(training_job_name):
    """Retrieve logs from model training job
    Args:
        training_job_name: model training job name (returned by POST call to '/training-jobs' route)
    Returns:
        model training job logs
    Raises:
        HTTPException: if job logs could not be retrieved
    """

    try:
        pod_list = core_v1.list_namespaced_pod(namespace).items
        pod_name = [
            pod.metadata.name
            for pod in pod_list
            if "job-name" in pod.metadata.labels
            and pod.metadata.labels["job-name"] == training_job_name
        ][0]
        logs = core_v1.read_namespaced_pod_log(name=pod_name, namespace=namespace)
        return logs

    except IndexError as e:
        raise HTTPException(
            status_code=400, detail=f"Job {training_job_name} not found"
        )


@app.get("/training-jobs")
def list_jobs(pipeline_step: Union[str, None] = None):
    """List training jobs and their statuses
    Args:
        pipeline: optional pipeline step name to filter training jobs
    Returns:
        list of training jobs executed thus far and current statuses
    Raises:
        HTTPException: if list of jobs could not be retrieved
    """

    try:
        if pipeline_step:
            dashed_pipeline_step = get_dashed_from_camel_cased(pipeline_step)
        else:
            dashed_pipeline_step = None
        output = []
        jobs = batch_v1.list_namespaced_job(namespace)
        for job in jobs.items:
            if job.metadata.name.startswith(
                f"model-training-{dashed_pipeline_step}"[:TRIMMED_JOB_PREFIX_LENGTH]
                if dashed_pipeline_step
                else "model-training"
            ):
                output.append({"name": job.metadata.name, "status": job.status})
        output.sort(reverse=True, key=lambda x: x["status"].start_time)
        print(json.dumps(output, indent=4, sort_keys=True, default=str))
        return json.dumps(output, indent=4, sort_keys=True, default=str)

    except ApiException as e:
        print(e, flush=True)
        raise HTTPException(status_code=500, detail=str(e))


@app.delete("/{training_job_name}")
def kill_job(training_job_name):
    """Delete model training job
    Args:
        training_job_name: model training job name to be deleted (returned by POST call to '/training-jobs' route)
    Returns:
        message indicating successful job deletion
    Raises:
        HTTPException: if job could not be deleted
    """

    try:
        batch_v1.delete_namespaced_job(training_job_name, namespace)
        return f"{training_job_name} successfully deleted."

    except ApiException as e:
        print(e, flush=True)
        raise HTTPException(status_code=500, detail=str(e))
