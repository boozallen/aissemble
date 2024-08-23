Feature: Migration MLFlow Dockerfile

  Scenario: Update the MLFlow Dockerfile to pull the community Docker image
    Given a Dockerfile is referencing the mlflow image from aissemble-mlflow
    When the 1.9.0 MLFlow Docker image migration executes
    Then the image will pull the community docker image

  Scenario: A Dockerfile not using MLFlow is not impacted by this migration
    Given a Dockerfile that is NOT referencing the mlflow image from aissemble-mlflow
    When the 1.9.0 MLFlow Docker image migration executes
    Then the image is unchanged