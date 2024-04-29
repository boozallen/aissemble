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
from mlflow.entities import Run, RunInfo
from unittest.mock import patch
from random import randint
import nose.tools as nt
import uuid
import os
import sys
from pathlib import Path

src_directory = Path(__file__).resolve().parents[4]
sys.path.append(str(src_directory) + "/main/python")
from model_versioning import version as model_versioning


@given("a model has been trained")
def step_impl(context):
    # set required environment variables for versioning
    os.environ["MLFLOW_TRACKING_URI"] = "test_mlflow_tracking_uri"
    os.environ["MODEL_DIRECTORY"] = "test_model_directory"
    os.environ["NEXUS_SERVER"] = "test_nexus_server"

    # create test mlflow training run info
    context.test_experiment_id = str(randint(0, 10))
    context.test_run_id = uuid.uuid4().hex
    context.test_training_run = create_test_training_run(
        context.test_experiment_id, context.test_run_id
    )


@when("I version the trained model")
def step_impl(context):
    # mocking call to get training run from mlflow since there is no way to get this info without going thru an entire training process.
    # mocking calls to run maven commands since we don't actually want to perform these commands when building the project.
    with patch("mlflow.get_run") as mock_mlflow_get_run:
        with patch("util.maven_util.package_and_deploy") as mock_mvn_package_and_deploy:
            with patch(
                "util.maven_util.evaluate_expression"
            ) as mock_mvn_evaluate_expression:
                # return test training run when mlflow.get_run is called
                mock_mlflow_get_run.return_value = context.test_training_run

                # make call to version the model
                model_versioning.version(context.test_run_id)

                # items to be validated in next step
                context.mock_mlflow_get_run = mock_mlflow_get_run
                context.mock_mvn_package_and_deploy = mock_mvn_package_and_deploy
                context.mock_mvn_evaluate_expression = mock_mvn_evaluate_expression


@then("the model artifacts are packaged and deployed")
def step_impl(context):
    # validate that mlflow.get_run was called with the expected training run id
    context.mock_mlflow_get_run.assert_called_once_with(context.test_run_id)

    # validate that mvn_util.package_and_deploy was called with the expected arguments
    context.mock_mvn_package_and_deploy.assert_called_once()
    call_args = context.mock_mvn_package_and_deploy.call_args
    actual_pom = call_args[0][0]
    actual_mvn_args = call_args[0][1]
    expected_model_pom = "model_versioning/model-pom.xml"
    expected_model_id = context.test_run_id
    expected_mlrun_dir = (
        os.environ["MLFLOW_TRACKING_URI"]
        + "/"
        + context.test_experiment_id
        + "/"
        + context.test_run_id
    )
    expected_model_dir = os.environ["MODEL_DIRECTORY"] + "/" + context.test_run_id
    expected_nexus_server = os.environ["NEXUS_SERVER"]
    nt.ok_(
        expected_model_pom == actual_pom,
        "Unexpected pom file name in maven_util.package_and_deploy call!",
    )
    nt.ok_(
        "-Dmodel.id=" + expected_model_id in actual_mvn_args,
        "Uexpected model.id arg in maven_util.package_and_deploy call!",
    )
    nt.ok_(
        "-Dmlrun.directory=" + expected_mlrun_dir in actual_mvn_args,
        "Uexpected mlrun.directory arg in maven_util.package_and_deploy call!",
    )
    nt.ok_(
        "-Dmodel.directory=" + expected_model_dir in actual_mvn_args,
        "Uexpected model.directory arg in maven_util.package_and_deploy call!",
    )
    nt.ok_(
        "-Dnexus.server=" + expected_nexus_server in actual_mvn_args,
        "Uexpected nexus.server arg in maven_util.package_and_deploy call!!",
    )

    # validate that mvn_util.evaluate_expression was called with the expected arguments
    context.mock_mvn_evaluate_expression.assert_called_once()
    call_args = context.mock_mvn_evaluate_expression.call_args
    actual_pom = call_args[0][0]
    actual_expression = call_args[0][1]
    actual_mvn_args = call_args[0][2]
    nt.ok_(
        expected_model_pom == actual_pom,
        "Unexpected pom file name in maven_util.evaluate_expression call!",
    )
    nt.ok_(
        "model.artifact.name" == actual_expression,
        "Unexpected pom file name in maven_util.evaluate_expression call!",
    )
    nt.ok_(
        "-Dmodel.id=" + expected_model_id in actual_mvn_args,
        "Uexpected model.id arg in maven_util.evaluate_expression call!",
    )


def create_test_training_run(experiment_id, run_id):
    run_info = RunInfo(
        run_id,
        experiment_id,
        "test_user",
        "test_status",
        "test_start_time",
        "test_end_time",
        "test_lifecycle_stage",
    )
    return Run(run_info, None)
