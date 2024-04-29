###
# #%L
# AIOps Docker Baseline::Versioning::Service
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from behave import *
from unittest import mock
import nose.tools as nt
import sys
from pathlib import Path
import uuid
import time
import json
from behave.api.async_step import async_run_until_complete
import os

src_directory = Path(__file__).resolve().parents[4]
sys.path.append(str(src_directory) + "/main/python")
MAX_JOB_NAME_LENGTH = 63


@given("I have Kubernetes cluster access")
def step_impl(context):
    properties = {
        "project_name": "project-name",
        "image_tag": "1.0.0",
        "docker_registry": "mock.docker.registry",
        "image_prefix": "mock-image-prefix",
        "namespace": "default",
        "image_pull_policy": "IfNotPresent",
    }
    with mock.patch("kubernetes.config.load_incluster_config"):
        with mock.patch(
            "krausening.properties.PropertyManager.get_properties",
            return_value=properties,
        ):
            with mock.patch("builtins.open", mock.mock_open(read_data="default")):
                from model_training_api import model_training_api

                context.model_training_api = model_training_api


@when("I trigger a training job")
@async_run_until_complete
async def step_impl(context):
    context.fixed_uuid = "29baa2ea-f486-4003-8d8d-608da4a071dc"

    mock_request = mock.Mock()
    mock_request.json = mock.AsyncMock(return_value={"hyperparameter_1": 1.0})

    mock_job = mock.Mock()
    mock_job.spec = mock.Mock()
    mock_job.spec.template = mock.Mock()
    mock_job.spec.template.spec = mock.Mock()
    mock_container = mock.Mock()
    mock_container.image = "boozallen/training-step:0.0.1"
    mock_job.spec.template.spec.containers = [mock_container]

    with mock.patch("uuid.uuid4", return_value=context.fixed_uuid) as _:
        with mock.patch(
            "kubernetes.client.BatchV1Api.create_namespaced_job"
        ) as create_kube_job_mock:
            training_job_name = await context.model_training_api.trigger_training(
                "testPipeline", mock_request
            )
            context.training_job_name = training_job_name
            create_kube_job_mock.assert_called_once()


@then("I get a valid model training job name returned")
def step_impl(context):
    nt.ok_(
        context.training_job_name
        == f"model-training-test-pipeli-{context.fixed_uuid}"[:MAX_JOB_NAME_LENGTH],
        "Job name not successfully returned.",
    )


@when("I request job logs")
def step_impl(context):
    training_job_name = (
        "model-training-test-pipeline-29baa2ea-f486-4003-8d8d-608da4a071dc"
    )
    context.job_logs = "Model training in progress..."

    mock_pod = mock.Mock()
    mock_pod.metadata = mock.Mock()
    mock_pod.metadata.name = f"{training_job_name}-{uuid.uuid4()}"
    mock_pod.metadata.labels = {"job-name": training_job_name}

    mock_pods = mock.Mock()
    mock_pods.items = [mock_pod]

    with mock.patch(
        "kubernetes.client.CoreV1Api.list_namespaced_pod", return_value=mock_pods
    ) as list_kube_pod_mock:
        with mock.patch(
            "kubernetes.client.CoreV1Api.read_namespaced_pod_log",
            return_value=context.job_logs,
        ) as get_kube_pod_logs_mock:
            logs = context.model_training_api.get_job_logs(training_job_name)
            context.retrieved_logs = logs
            list_kube_pod_mock.assert_called_once()
            get_kube_pod_logs_mock.assert_called_once()


@then("I get logs returned")
def step_impl(context):
    nt.ok_(
        context.retrieved_logs == context.job_logs,
        "Logs not successfully returned.",
    )


@when("I request a list of training jobs")
def step_impl(context):
    context.job_name = (
        "model-training-test-pipeline-29baa2ea-f486-4003-8d8d-608da4a071dc"
    )
    context.job_status = mock.Mock()
    context.job_status.start_time = time.time()

    mock_job = mock.Mock()
    mock_job.metadata = mock.Mock()
    mock_job.metadata.name = context.job_name
    mock_job.status = context.job_status

    mock_jobs = mock.Mock()
    mock_jobs.items = [mock_job]

    with mock.patch(
        "kubernetes.client.BatchV1Api.list_namespaced_job", return_value=mock_jobs
    ) as list_kube_job_mock:
        training_job_list = context.model_training_api.list_jobs()
        context.training_job_list = training_job_list
        list_kube_job_mock.assert_called_once()


@then("I get a list of training jobs returned")
def step_impl(context):
    for training_job in json.loads(context.training_job_list):
        nt.ok_(
            "name" in training_job and "status" in training_job,
            "Invalid job list returned.",
        )


@when("I kill a training job")
def step_impl(context):
    context.training_job_name = (
        "model-training-test-pipeline-29baa2ea-f486-4003-8d8d-608da4a071dc"
    )

    with mock.patch(
        "kubernetes.client.BatchV1Api.delete_namespaced_job"
    ) as delete_kube_job_mock:
        context.return_message = context.model_training_api.kill_job(
            context.training_job_name
        )
        delete_kube_job_mock.assert_called_once()


@then("I get a message indicating the job has been deleted")
def step_impl(context):
    nt.ok_(
        context.return_message == f"{context.training_job_name} successfully deleted.",
        "Job deleted message not successfully returned.",
    )
