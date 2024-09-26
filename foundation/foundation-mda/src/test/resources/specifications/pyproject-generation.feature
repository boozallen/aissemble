@pyproject-generation
Feature: Minimum Python version can be set in the pyproject.toml.vm files.

  @pipeline-generation @code-generation
  Scenario: The data-flow pipeline generates the correct pyproject.toml
    Given a project with the name "example"
    And a "data-flow" pipeline using the "data-delivery-pyspark" implementation
    When the "pipeline" profile "data-delivery-pyspark-pipeline" is generated
    Then the "pyproject.toml" is generated with the minimum Python version, ">=3.8"

   @pipeline-generation @code-generation
   Scenario Outline: The record metamodel with profile "<profile>" generates the correct pyproject.toml
    Given a project with the name "example"
    And a record model with a corresponding dictionary type defined
    When the "metadata" profile "<profile>" is generated
    Then the "pyproject.toml" is generated with the minimum Python version, ">=3.8"

    Examples:
      | profile                                    |
      | data-delivery-combined-data-records-python |
      | data-delivery-data-records-core-python     |
      | data-delivery-data-pyspark                 |


  @pipeline-generation @code-generation
  Scenario Outline: The machine-learning pipeline with the "<pipelineStepType>" step generates the correct pyproject.toml
    Given a project with the name "example"
    And a "machine-learning" pipeline with step type "<pipelineStepType>" using the "machine-learning-mlflow" implementation
    When the "pipeline" profile "<profile>" is generated
    Then the "pyproject.toml" is generated with the minimum Python version, ">=3.8"

    Examples:
      | pipelineStepType   | profile                             |
      | training           | machine-learning-training           |
      | sagemaker-training | machine-learning-sagemaker-training | 
      | inference          | machine-learning-inference          |
