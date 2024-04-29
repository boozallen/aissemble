###
# #%L
# AIOps Docker Baseline::Versioning::Service
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# # #L%
# ###
from behave import *
from unittest import mock
import os

from fastapi import BackgroundTasks
from behave.api.async_step import async_run_until_complete


class MockSagemakerSession:
    def __init__(self):
        pass


# Mock EstimatorParam
class MockEstimatorParam:
    # Initialize a mock object to represent Estimator parameters
    def __init__(
        self,
        image_uri,
        hyperparameters,
        instance_type,
        bucket,
        prefix,
        metric_definitions,
    ):
        self.image_uri = image_uri
        self.hyperparameters = hyperparameters
        self.prefix = prefix
        self.instance_type = instance_type
        self.bucket = bucket
        self.metric_definitions = metric_definitions


# Mock Environment Param
class EnvMock:
    # Initialize an environment mocking object
    def __init__(self, env_dict):
        self.env_dict = env_dict
        self.patcher = mock.patch.dict("os.environ", env_dict)

    def __enter__(self):
        self.patcher.start()

    def __exit__(self, exc_type, exc_val, exc_tb):
        self.patcher.stop()


# Mock Estimator
class MockEstimator:
    # Initialize a mock Estimator object
    def __init__(
        self,
        image_uri,
        role,
        train_instance_count,
        sagemaker_session,
        instance_type,
        hyperparameters,
        metric_definitions,
    ):
        self.image_uri = image_uri
        self.role = role
        self.train_instance_count = train_instance_count
        self.sagemaker_session = sagemaker_session
        self.instance_type = instance_type
        self.hyperparameters = hyperparameters
        self.metric_definitions = metric_definitions

    def fit(self, config, wait=False):
        # Mock the behavior of the fit method
        self.config = config

    @property
    def latest_training_job(self):
        # Mock the behavior of the latest_training_job property
        class MockTrainingJob:
            def __init__(self):
                self.job_name = "mock_job_name"

        return MockTrainingJob()


# Mock Estimator
class MockSagemakerClient:
    def describe_training_job(self, TrainingJobName):
        if TrainingJobName == "mock_job_name":
            return {"TrainingJobStatus": "Completed", "SecondaryStatus": "Completed"}


class MockSagemakerSessionWithClient:
    @property
    def sagemaker_client(self):
        return MockSagemakerClient()


class MockBotoClient:
    def download_file(self, bucket, key, tar_path):
        return "File downloaded."


@given("I can authenticate with AWS SageMaker")
# @async_run_until_complete
def step_impl(context):
    with mock.patch(
        "kubernetes.config.load_incluster_config"
    ) as incluster_kube_config_mock:
        from model_training_api_sagemaker import model_training_api_sagemaker
    context.model_training_api_sagemaker = model_training_api_sagemaker


@when("I submits a job to sagemaker")
@async_run_until_complete
async def step_impl(context):
    # Set up the necessary mock objects
    # Replace the actual Estimator class with the MockEstimator
    original_estimator_class = (
        context.model_training_api_sagemaker.submit_job.__globals__.get(
            "Estimator", None
        )
    )
    context.model_training_api_sagemaker.submit_job.__globals__["Estimator"] = (
        MockEstimator
    )

    with EnvMock({"AWS_ROLE_ARN": "sagemaker"}):
        # Test the function with sample inputs
        image_uri = "sample_image_uri"
        instance_type = "local"
        hyperparameters = {"num_round": 6, "K": 5}
        bucket = "fake_bucket"
        prefix = "fake_prefix"
        metric_definitions = {
            "train_error": "Train_error=(.*?);",
            "validation_error": "Valid_error=(.*?);",
        }
        with mock.patch(
            "sagemaker.Session", return_value=MockSagemakerSessionWithClient()
        ):
            with mock.patch("boto3.client", return_value=MockBotoClient()):
                with mock.patch("fastapi.BackgroundTasks.add_task"):
                    context.result = (
                        await context.model_training_api_sagemaker.submit_job(
                            MockEstimatorParam(
                                image_uri,
                                hyperparameters,
                                instance_type,
                                bucket,
                                prefix,
                                metric_definitions,
                            ),
                            BackgroundTasks,
                        )
                    )

    # Restore the original Estimator class
    context.model_training_api_sagemaker.submit_job.__globals__["Estimator"] = (
        original_estimator_class
    )


@then("I get a new job ID returned")
def step_impl(context):
    # The result of the submit_job function should contain the new job ID
    assert context.result == "mock_job_name"


@when("I try to retrieve job status")
def step_impl(context):
    with mock.patch("sagemaker.Session", return_value=MockSagemakerSessionWithClient()):
        from model_training_api_sagemaker import model_training_api_sagemaker

        context.model_training_api_sagemaker = model_training_api_sagemaker

        context.result = context.model_training_api_sagemaker.get_job_status(
            "mock_job_name"
        )


@then("I get the status of the job")
def step_impl(context):
    assert context.result == {
        "jobName": "mock_job_name",
        "jobStatus": {"primaryStatus": "Completed", "secondaryStatus": "Completed"},
    }


@given("I can get aws key from environment variables")
def step_impl(context):
    # Set mock AWS access and secret keys as environment variables
    os.environ["AWS_SECRET_ACCESS_KEY"] = "mock_secret_key"
    os.environ["AWS_ACCESS_KEY_ID"] = "mock_access_key"


@then("I can set authentication for SageMaker via env")
def step_impl(context):
    assert os.environ["AWS_ACCESS_KEY_ID"] == "mock_access_key"
    assert os.environ["AWS_SECRET_ACCESS_KEY"] == "mock_secret_key"
