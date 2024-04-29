@mlflow-migration
Feature: Mlflow v2 migration to add new external s3 config
  Scenario Outline: Migrate a mlflow v2 deployment
    Given a project that has a mlflow v2 deployment
    And the values.yaml and values-dev.yaml do not contain "<yaml-config>" configuration
    When the mlflow yaml migration executes
    Then the hardcoded Localstack credentials are added to the values-dev.yaml
    And the Localstack secret credentials are added to the values.yaml

  Examples:
    | yaml-config      |
    | aissemble-mlflow |
    | mlflow           |
    | externalS3       |

  Scenario Outline: Skip mlflow v2 deployment migrations if credentials are set
    Given a project that has a mlflow v2 deployment
    And the "<file>" contains external S3 configuration
    When the mlflow yaml migration executes
    Then the mlflow yaml migration is skipped
  
    Examples:
    | file       |
    | values     |
    | values-dev |

  Scenario: Skip mlflow v2 migrations when helm dependencies are not present
    Given a project that does not contain any helm dependencies
    When the mlflow yaml migration executes
    Then the mlflow yaml migration is skipped
  
  Scenario: Skip mlflow v2 migrations when mlflow v2 dependency is not present
    Given a project that does not contain a mlflow v2 dependency
    When the mlflow yaml migration executes
    Then the mlflow yaml migration is skipped