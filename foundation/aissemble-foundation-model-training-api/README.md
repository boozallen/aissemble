## aiSSEMBLE&trade; Model Training API

[![PyPI](https://img.shields.io/pypi/v/aissemble-foundation-model-training-api?logo=python&logoColor=gold)](https://pypi.org/project/aissemble-foundation-model-training-api/)
![PyPI - Python Version](https://img.shields.io/pypi/pyversions/aissemble-foundation-model-training-api?logo=python&logoColor=gold)
![PyPI - Wheel](https://img.shields.io/pypi/wheel/aissemble-foundation-model-training-api?logo=python&logoColor=gold)

This module contains the implementation and baseline Docker image for the aiSSEMBLE model training service. This service allows you to create model training jobs, list jobs, retrieve job logs, and kill jobs.

### Model Training API

`POST /training-jobs?pipeline=PIPELINE_NAME`

- Request body contains all key/value pairs required for model training, such as model hyperparameters
- Functionality:
    - Spawns appropriate model training Kubernetes job
        - Checks for existence of model training image with naming convention: "model-training-PIPELINE_NAME"
            - Returns error if not present
        - Job naming convention: "model-training-PIPELINE_NAME-RANDOM_UUID"
    - Passes in user-provided parameters 
- Returns model training job name 

`GET /training-jobs/TRAINING_JOB_NAME`

- Returns logs from pod running model training job or error if job doesn't exist

`GET /training-jobs`

- Returns list of all model training jobs (active, failed, and completed) and statuses
- Filters all jobs in cluster by reserved job name prefix "model-training"

`GET /training-jobs?pipeline=PIPELINE_NAME`

- Returns list of all model training jobs (active, failed, and completed) and statuses for a given pipeline

`DELETE /training-jobs/TRAINING_JOB_NAME`

- Deletes specified Kubernetes job
- Returns error if job does not exist

### Remaining Items
- Ensure appropriate Kubernetes RBAC config in Helm charts
- Deploy model training API in downstream projects with ML training step(s)
- In downstream projects, ensure model training image is generated into "model-training-PIPELINE_NAME"
- In downstream projects, ensure embeddings deployment name is "PIPELINE_NAME-STEP_NAME"
- Configure permissions/implement PDP authorization for each API route