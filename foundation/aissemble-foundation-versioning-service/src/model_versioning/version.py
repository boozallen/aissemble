###
# #%L
# AIOps Docker Baseline::Versioning::Service
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
import os
import mlflow
from util import maven_util

mlrun_artifacts_path = "{mlflow_tracking_uri}/{experiment_id}/{run_id}"
model_artifacts_path = "{model_directory}/{run_id}"


def version(run_id):
    print("Versioning model from training run %s..." % run_id)

    # check that the required variables for versioning are set
    mlflow_tracking_uri = os.environ.get("MLFLOW_TRACKING_URI", None)
    model_directory = os.environ.get("MODEL_DIRECTORY", None)
    nexus_server = os.environ.get("NEXUS_SERVER", None)
    validate_versioning_variables(mlflow_tracking_uri, model_directory, nexus_server)

    # get the mlflow experiment id from the run details
    mlflow.set_tracking_uri(mlflow_tracking_uri)
    mlrun = mlflow.get_run(run_id)
    experiment_id = mlrun.info.experiment_id

    # package and deploy the mlrun and model artifacts
    mlrun_artifacts = mlrun_artifacts_path.format(
        mlflow_tracking_uri=mlflow_tracking_uri,
        experiment_id=experiment_id,
        run_id=run_id,
    )
    model_artifacts = model_artifacts_path.format(
        model_directory=model_directory, run_id=run_id
    )
    versioned_artifact = package_and_deploy(
        run_id, mlrun_artifacts, model_artifacts, nexus_server
    )

    print("Successfully versioned model:", versioned_artifact)

    return versioned_artifact


def validate_versioning_variables(mlflow_tracking_uri, model_directory, nexus_server):
    errors = []

    if not mlflow_tracking_uri:
        errors.append("MLFLOW_TRACKING_URI is not set")
    if not model_directory:
        errors.append("Missing MODEL_DIRECTORY is not set")
    if not nexus_server:
        errors.append("Missing NEXUS_SERVER is not set")

    if errors:
        raise Exception(
            "Unable to version model due to the following errors: " + str(errors)
        )


def package_and_deploy(run_id, mlrun_artifacts, model_artifacts, nexus_server):
    model_pom = "model_versioning/model-pom.xml"
    mlrun_dir_arg = "-Dmlrun.directory=" + mlrun_artifacts
    model_dir_arg = "-Dmodel.directory=" + model_artifacts
    model_id_arg = "-Dmodel.id=" + run_id
    nexus_server_arg = "-Dnexus.server=" + nexus_server
    args = [mlrun_dir_arg, model_dir_arg, model_id_arg, nexus_server_arg]

    # zip up model artifacts and deploy to nexus
    maven_util.package_and_deploy(model_pom, args)

    # return the packaged artifact name
    return maven_util.evaluate_expression(
        model_pom, "model.artifact.name", [model_id_arg]
    )
