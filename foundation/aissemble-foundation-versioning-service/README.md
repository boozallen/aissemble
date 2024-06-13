## aiSSEMBLE&trade; Foundation Versioning

[![PyPI](https://img.shields.io/pypi/v/aissemble-foundation-versioning-service?logo=python&logoColor=gold)](https://pypi.org/project/aissemble-foundation-versioning-service/)
![PyPI - Python Version](https://img.shields.io/pypi/pyversions/aissemble-foundation-versioning-service?logo=python&logoColor=gold)
![PyPI - Wheel](https://img.shields.io/pypi/wheel/aissemble-foundation-versioning-service?logo=python&logoColor=gold)

This module contains the implementation for the aiSSEMBLE versioning service. The service provides the ability to version artifacts produced by a Solution Baseline machine-learning pipeline, and uses Maven to package and deploy the artifacts to Nexus.

### Model Versioning

The following GET endpoint is provided for model versioning: `/version/model/{run_id}`

`run_id` is the unique training run identifier assigned to an MLflow training experiment. The versioning service currently only supports models that have been trained using the MLflow implementation of a Solution Baseline machine-learning pipeline. It will package the training run artifacts produced by MLflow and the model artifacts produced when saving through MLflow.

The following environment variables are required for model versioning:
- `MLFLOW_TRACKING_URI`: the MLflow tracking URI path specified in the training pipeline. This is where MLflow saves the training run artifacts.
- `MODEL_DIRECTORY`: The model directory path specified in the training pipeline. This is where the training pipeline saves the trained models.
- `NEXUS_SERVER`: The Nexus server to use to push the versioned artifact to. The credentials for this server must be provided in the Maven settings.xml file.

### Example

Below is a basic example of how to leverage the versioning service:
- Extending the baseline docker image to include Nexus credentials
    ```dockerfile
    FROM boozallen/aissemble-versioning:${VERSION_AISSEMBLE}

    COPY /custom/maven/settings/containing/nexus/credentials.xml /root/.m2/settings.xml
    ```
- Docker-compose configurations for running the service
    ```
    example-versioning-service:
        image: my-extended-versioning-service-image:latest
        ports:
          - '80:80'
        environment:
            MLFLOW_TRACKING_URI: /path/to/mlflow/tracking/uri
            MODEL_DIRECTORY: /path/to/saved/models
            NEXUS_SERVER: http://nexus-server:port
    ```
- Note: By default Versioning requires authorization and therefore must have security services running (policy-decision-point).
    - Add the following to the above docker-compose file:
        ```
        policy-decision-point:
            image: ${CONTAINER_REGISTRY}boozallen/policy-decision-point-docker:latest
            hostname: policy-decision-point
            container_name: policy-decision-point
            ports:
              - "8780:8080"
              - "9700:9000"
        ```
    - Add an `auth.properties` file to your docker image with the following content.
        ```
        pdp_host_url=http://policy-decision-point:8080/api/pdp
        ```
    - Then add the following environment variable that points to your properties file.  
      `ENV KRAUSENING_BASE /path/to/auth.properties/file`
    - To disable authorization you can add the following to the `auth.properties` file: `is_authorization_enabled = False`.
