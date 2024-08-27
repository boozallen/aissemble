Feature: Migration Airflow Dockerfile

  Scenario: Update the Airflow Dockerfile to pull the community Docker image
    Given a Dockerfile is referencing the airflow image from aissemble-airflow
    When the 1.9.0 Airflow Docker image migration executes
    Then the Dockerfile will pull the community docker Airflow image

  Scenario: Do not update the Airflow Dockerfile when the file is already pulling the community Airflow Docker image
    Given a Docker file is referencing the community airflow image
    When the 1.9.0 Airflow Docker image migration executes
    Then the airflow migration is skipped

  Scenario: Skip Dockerfiles that are not Airflow related
    Given a Docker file is not referencing Airflow
    When the 1.9.0 Airflow Docker image migration executes
    Then the airflow migration is skipped