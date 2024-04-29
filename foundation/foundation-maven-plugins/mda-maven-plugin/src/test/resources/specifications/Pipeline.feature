@Pipeline @CopyArtifacts
Feature: As an aiSSEMBLE user, I want pipeline artifacts derived from MDA models so I don't have to manually manage the pom

  Scenario Outline: The user can copy data delivery pipeline artifacts based on the MDA model
    Given a "<implementation>" pipeline model
    And a target output directory
    And the plugin is configured to target "<type>" pipelines with "<artifactType>" artifacts
    When the copy-pipeline-artifacts goal is run
    Then the pipeline model is read
    And the "<fileTypes>" file(s) from the pipeline will be moved to the target directory

    Examples:
      | implementation          | type                      | artifactType         | fileTypes           |
      | data-delivery-spark     | data-flow                 | DEFAULT              | .jar                |
      | data-delivery-pyspark   | data-flow                 | DEFAULT              | .tar.gz,.txt,.py    |
      | data-delivery-pyspark   | data-flow                 | VALUES_FILES         | .yaml               |
      | data-delivery-spark     | data-flow                 | VALUES_FILES         | .yaml               |
      | machine-learning-mlflow | machine-learning-training | DEFAULT              | .tar.gz,.txt        |
      | machine-learning-mlflow | sagemaker-training        | DEFAULT              | .tar.gz,.txt        |

  Scenario: Copy python data-record artifacts
    Given a "data-delivery-pyspark" pipeline model
    And a dictionary and 0 or more record models
    And a data records directory "src/test/resources/data-records"
    And a list of data record modules:
      | data-records-core  |
    And a target output directory
    And the plugin is configured to target "data-flow" pipelines with "DEFAULT" artifacts
    When the copy-pipeline-artifacts goal is run
    Then the pipeline model is read
    And the ".tar.gz,.txt,.py" file(s) from the pipeline will be moved to the target directory
    And the ".whl" file(s) from each data record module will be moved to the target directory

  # Note that this test is showing that the `data-records-core-java` module in the test/resources directory is not included
  Scenario: Discover python data-record artifacts
    Given a "data-delivery-pyspark" pipeline model
    And a dictionary and 0 or more record models
    And a data records directory "src/test/resources/data-records"
    And a target output directory
    And the plugin is configured to target "data-flow" pipelines with "DEFAULT" artifacts
    When the copy-pipeline-artifacts goal is run
    Then the pipeline model is read
    And the ".whl" file(s) from "data-records-core" module will be moved to the target directory
    And the ".whl" file(s) from "data-records-spark" module will be moved to the target directory