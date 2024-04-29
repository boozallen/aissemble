###
# #%L
# AIOps Docker Baseline::Model Training API::Service
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from fastapi import FastAPI, HTTPException, BackgroundTasks
from pydantic import BaseModel
import sagemaker
from sagemaker.analytics import TrainingJobAnalytics
from sagemaker.estimator import Estimator
import os
import mlflow
import time
import boto3
import tempfile

SERVICE_ACCOUNT_NAME = "model-training-api-sagemaker"

app = FastAPI()
mlflow.set_tracking_uri(
    os.getenv(key="MLFLOW_TRACKING_URI", default="http://mlflow-ui-tracking:5005")
)


class EstimatorParam(BaseModel):
    image_uri: str
    hyperparameters: dict
    instance_type: str
    bucket: str
    prefix: str
    metric_definitions: dict


def retrieve_and_log_metrics(job_name, previous_metrics_set):
    """
    Retrieves metrics from SageMaker, logs them to MLflow.
    Parameters:
        job_name (str): SageMaker training job name.
        previous_metrics_set (set): Set of previously retrieved training metrics.
    Returns:
        set: Set of training job metrics retrieved from SageMaker API.
    """

    # Retrieve newest training job metrics from SageMaker API as DataFrame
    metrics_df = TrainingJobAnalytics(training_job_name=job_name).dataframe()

    # Convert DataFrame to set of tuples for comparison with previous metrics
    metrics_dict = metrics_df.to_dict(orient="split")
    metrics_set = set(tuple(x) for x in metrics_dict["data"])

    # Use set difference to identify newly-retrieved metrics
    # Loop over newly-retrieved metrics and log them to MLflow
    for metric in metrics_set - previous_metrics_set:
        mlflow.log_metric(metric[1], float(metric[2]))

    return metrics_set


def retrieve_and_log_model(job_description, s3_client):
    """
    Retrieves model artifacts from SageMaker, logs them to MLflow.
    Parameters:
        job_description (dict): Job description retrieved from SageMaker API.
        s3_client (boto3.S3.Client): Initialized boto3 S3 client, will be used to download model artifacts.
    """

    try:
        s3_uri_split = job_description["ModelArtifacts"]["S3ModelArtifacts"].split("/")
        bucket = s3_uri_split[2]
        key = "/".join(s3_uri_split[3:])

        # Create temporary directory which is automatically removed when "with" block completes
        with tempfile.TemporaryDirectory() as tmp_dir:
            # Download model artifacts from S3
            tar_path = os.path.join(tmp_dir, "model.tar.gz")
            s3_client.download_file(bucket, key, tar_path)

            # Log model artifacts to MLflow
            mlflow.log_artifact(tar_path)
    except KeyError:
        log_error("No model artifacts found.")


def log_error(error_message):
    """
    Logs error message text file to MLflow.
    Parameters:
        error_message (str): SageMaker training job name.
    """

    # Create temporary directory which is automatically removed when "with" block completes
    with tempfile.TemporaryDirectory() as tmp_dir:
        # Write error message file within temporary directory
        error_file_path = os.path.join(tmp_dir, "error.txt")
        with open(error_file_path, "w") as f:
            f.write(error_message)

        # Log error message file to MLflow
        mlflow.log_artifact(error_file_path)


def monitor_training_job(job_name, estimator_param, sagemaker_client, s3_client):
    """
    Creates MLflow run, retrieves and logs metrics and artifacts. To be run as Background Task.
    Parameters:
        job_name (str): SageMaker training job name.
        estimator_param (EstimatorParam): Contains user-provided training job parameters.
        sagemaker_client (boto3.SageMaker.Client): Initialized SageMaker client, will be used to retrieve job details.
        s3_client (boto3.S3.Client): Initialized boto3 S3 client, will be used to download model artifacts.
    """

    params = {
        "image_uri": estimator_param.image_uri,
        "bucket": estimator_param.bucket,
        "data_prefix": estimator_param.prefix,
    } | estimator_param.hyperparameters

    previous_metrics_set = set()
    with mlflow.start_run(run_name=job_name):
        mlflow.log_params(params)
        status = "InProgress"
        while status == "InProgress":
            job_description = sagemaker_client.describe_training_job(
                TrainingJobName=job_name
            )
            status = job_description["TrainingJobStatus"]
            if status == "Completed":
                retrieve_and_log_model(job_description, s3_client)
            elif status == "Failed":
                log_error(job_description["FailureReason"])
                mlflow.end_run(status="Failed")
            elif status == "Stopped":
                log_error(
                    "Job stopped by SageMaker platform, view logs via AWS console."
                )
                mlflow.end_run(status="Stopped")
            else:
                try:
                    previous_metrics_set = retrieve_and_log_metrics(
                        job_name, previous_metrics_set
                    )
                    time.sleep(5)
                except KeyError:
                    # KeyError will be thrown until image is downloaded to instance and training begins
                    time.sleep(5)


@app.post("/sagemaker-training-jobs")
async def submit_job(
    estimator_param: EstimatorParam, background_tasks: BackgroundTasks
):
    """
    Endpoint to submit a job to AWS SageMaker for model training.
    Parameters:
        image_uri (str): The URI of the container image to use for training.
        hyperparameters (dict): A dictionary of hyperparameters for the model training job.
        instance_type (str): The EC2 instance type to use for training the model.
        bucket (str): The name of the S3 bucket where the training data is stored.
        prefix (str): The prefix path within the S3 bucket where the training data is located.
        metric_definitions (dict): A dictionary of metrics to be monitored during training.
    Returns:
        str: The name of the latest training job.
    """

    s3_client = boto3.client("s3")
    sagemaker_session = sagemaker.Session()
    sagemaker_client = sagemaker_session.sagemaker_client

    metric_definitions = []
    for metric_name, metric_regex in estimator_param.metric_definitions.items():
        metric_definitions.append({"Name": metric_name, "Regex": metric_regex})

    aws_role_arn = os.environ["AWS_ROLE_ARN"]
    estimator = Estimator(
        image_uri=estimator_param.image_uri,
        train_instance_count=1,
        instance_type=estimator_param.instance_type,
        sagemaker_session=sagemaker_session,
        hyperparameters=estimator_param.hyperparameters,
        role=aws_role_arn,
        metric_definitions=metric_definitions,
    )

    train_config = sagemaker.inputs.TrainingInput(
        "s3://{0}/{1}/train/".format(estimator_param.bucket, estimator_param.prefix),
        content_type="text/csv",
    )
    val_config = sagemaker.inputs.TrainingInput(
        "s3://{0}/{1}/val/".format(estimator_param.bucket, estimator_param.prefix),
        content_type="text/csv",
    )
    test_config = sagemaker.inputs.TrainingInput(
        "s3://{0}/{1}/test/".format(estimator_param.bucket, estimator_param.prefix),
        content_type="text/csv",
    )

    estimator.fit(
        {"train": train_config, "validation": val_config, "test": test_config},
        wait=False,
    )

    job_name = estimator.latest_training_job.job_name
    background_tasks.add_task(
        monitor_training_job, job_name, estimator_param, sagemaker_client, s3_client
    )
    return job_name


@app.get("/{sagemaker_job_name}")
def get_job_status(sagemaker_job_name):
    """
    Get the status of a SageMaker training job.
    Parameters:
        job_name (str): The name of the training job.
    Returns:
        dict: A JSON response containing the job name and its status.
    """
    try:
        sagemaker_session = sagemaker.Session()
        sagemaker_client = sagemaker_session.sagemaker_client

        # Get the training job description
        response = sagemaker_client.describe_training_job(
            TrainingJobName=sagemaker_job_name
        )

        status_output = {
            "primaryStatus": response["TrainingJobStatus"],
            "secondaryStatus": response["SecondaryStatus"],
        }

        # Extract and return the job status
        return {"jobName": sagemaker_job_name, "jobStatus": status_output}

    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Job not retrieved: An error occurred while getting the status of the training job: {str(e)}",
        )


# %%
