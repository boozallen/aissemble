## aiSSEMBLE Model Training API

This module contains the implementation and baseline Docker image for the aiSSEMBLE model training and model training sagemaker service. This service allows you to submit sagemaker model training jobs and retrieves the status of a specific sagemaker traning job

### Model Training API Sagemaker

`POST /sagemaker-training-jobs?pipeline=PIPELINE_NAME`

* Submits a new SageMaker training job with the provided configurations. The training data is expected to be located in S3 with the provided bucket and prefix.
* Returns the name of the latest training job as a response.


`GET /training-jobs/TRAINING_JOB_NAME`

* Retrieves the status of a specific SageMaker training job.
* Returns a JSON response containing the job name and its status.
* Returns 500 error if jobs statuses cannot be retrieved

