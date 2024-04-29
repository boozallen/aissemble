@manual
# We don't have a current template for testing, so this file is for documentation purposes only
Feature: Generating simple DAGs for pipelines

  Scenario: Create a valid DAG file for Data Delivery pipelines
    Given a data delivery pipeline model
    And airflow as the execution helper
    When the pipeline is read
    Then a valid DAG is generated for executing the pipeline

  Scenario: Do not create a DAG file for Data Delivery pipelines
    Given a data delivery pipeline model
    And the execution helper is not specified
    When the pipeline is read
    Then a DAG is  not generated

  Scenario: Create a valid DAG file for Machine Learning pipelines
    Given a machine learning pipeline model
    And a training step
    When the pipeline is read
    Then a valid DAG is generated for executing the pipeline

  Scenario: Create multiple valid DAG files for Machine Learning pipelines
    Given a machine learning pipeline model
    And multiple training steps
    When the pipeline is read
    Then a valid DAG is generated for executing each training step

  Scenario: Do not create a DAG file for Machine Learning pipelines
    Given a machine learning pipeline model
    And the model does not contain any training steps
    When the pipeline is read
    Then a DAG is not generated

