@charts-v1
Feature: As an aiSSEMBLE user, I want my v1 chart images updated to the latest aiSSEMBLE version automatically so upgrade errors are minimized

  Background:
    #Note: pipeline-invocation-service is not a v1 chart but needs its tag updated in values.yaml anyways
    Given a v1 chart
      | spark-operator              |
      | spark-infrastructure        |
      | jenkins                     |
      | kafka-cluster               |
      | model-training-api          |
      | hive-metastore-db           |
      | hive-metastore-service      |
      | metadata                    |
      | pipeline-invocation-service |

  Scenario: Upgrading v1 aiSSEMBLE charts to the latest version
    Given the image tag in the values file is less than the current version of aiSSEMBLE
    When the v1 helm chart migration executes
    Then the version of the chart is updated to the current version in the values configuration

  Scenario: Upgrading v1 aiSSEMBLE charts to the latest version when no hostname is present (check image name)
    Given the values file does not contain a hostname
    And the image tag in the values file is less than the current version of aiSSEMBLE
    When the v1 helm chart migration executes
    Then the version of the chart is updated to the current version in the values configuration

  Scenario: Upgrading v1 aiSSEMBLE charts to the latest version when no hostname or image name is present (check repo)
    Given the values file does not contain a hostname or image name
    And the image tag in the values file is less than the current version of aiSSEMBLE
    When the v1 helm chart migration executes
    Then the version of the chart is updated to the current version in the values configuration

  Scenario: Upgrading v1 aiSSEMBLE charts to the latest version when more properties are set after the image tag
    Given the values file contains more properties after the image tag
    And the image tag in the values file is less than the current version of aiSSEMBLE
    When the v1 helm chart migration executes
    Then the version of the chart is updated to the current version in the values configuration

  Scenario: Skipping upgrade if aiSSEMBLE version in pom and in v1 chart file match
    Given the image tag in the values file is equal to the current version of aiSSEMBLE
    When the v1 helm chart migration executes
    Then the chart file aiSSEMBLE upgrade is skipped

  Scenario: Skipping upgrade if aiSSEMBLE version in pom is less than the version in the v1 chart file
    Given the image tag in the values file is greater than the current version of aiSSEMBLE
    When the v1 helm chart migration executes
    Then the chart file aiSSEMBLE upgrade is skipped
