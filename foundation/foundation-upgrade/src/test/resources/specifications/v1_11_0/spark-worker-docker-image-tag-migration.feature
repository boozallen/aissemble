Feature: Pipeline Spark Worker Docker Image Tag Migration

  Scenario: Migrate a pipeline yaml when spark worker image tag is latest
    Given a pipeline spark application yaml has image configuration with "<tag>" tag
    When the 1.11.0 pipeline spark application image tag migration executes
    Then the image is tagged with project version

  Examples:
        | tag       |
        | latest    |
        |           |

  Scenario: Skip a pipeline yaml image tag migration when the spark work image tag is not set to latest
    Given a pipeline spark application yaml has image configuration with version tag
    When the 1.11.0 pipeline spark application image tag migration executes
    Then the pipeline spark application image tag migration is skipped