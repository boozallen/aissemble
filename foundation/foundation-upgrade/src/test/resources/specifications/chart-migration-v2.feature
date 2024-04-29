@chart-v2-migration
Feature: As an aiSSEMBLE user, I want my chart dependencies updated to the latest aiSSEMBLE version automatically so upgrade errors are minimized

  Scenario: Upgrading aiSSEMBLE subcharts to the latest version.
    Given a helm chart with an aiSSEMBLE dependency
    And the dependency is on a version less than the current version of aiSSEMBLE
    When the helm chart migration executes
    Then the version of the dependency is updated to the current version

  Scenario: Skipping upgrade if aiSSEMBLE version in pom and in dependencies match
    Given a helm chart with an aiSSEMBLE dependency
    And the dependency is on a version equal to the current version of aiSSEMBLE
    When the helm chart migration executes
    Then the chart file aiSSEMBLE upgrade is skipped

  Scenario: Skipping upgrade if aiSSEMBLE version in pom is less than the version in the chart file's aiSSEMBLE dependencies
    Given a helm chart with an aiSSEMBLE dependency
    And the dependency is on a version greater than the current version of aiSSEMBLE
    When the helm chart migration executes
    Then the chart file aiSSEMBLE upgrade is skipped

  Scenario: Upgrading aiSSEMBLE dependencies to the latest version if latest version is a snapshot
    Given a helm chart with an aiSSEMBLE dependency
    And the dependency is on a version less than the current snapshot version of aiSSEMBLE
    When the helm chart migration executes
    Then the version of the dependency is updated to the current version

  Scenario: Skipping upgrade if chart has no aiSSEMBLE dependencies
    Given a helm chart with no aiSSEMBLE dependencies
    And the dependency is on a version less than the current version of aiSSEMBLE
    When the helm chart migration executes
    Then the chart file aiSSEMBLE upgrade is skipped
