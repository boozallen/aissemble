@chart-name-change-migration
Feature: As an aiSSEMBLE user, I want my aiSSEMBLE Helm charts updated to the latest naming convention automatically

  Scenario: Upgrade aiSSEMBLE Helm chart value file to use to latest naming convention
    Given a value file using the older Helm chart naming convention
    When the Helm chart name change migration executes
    Then the value file is migrated to use new naming convention

  Scenario: Upgrade aiSSEMBLE Helm chart Chart file to use to latest naming convention
    Given a Chart file using the older Helm chart naming convention
    When the Helm chart name change migration executes
    Then the Chart file is migrated to use new naming convention

  Scenario: Skipping Value file migration if values file already uses new naming convention
    Given a value file using the new Helm chart naming convention
    When the Helm chart name change migration executes
    Then the value file migration is skipped

  Scenario: Skipping Chart file migration if Chart file already uses new naming convention
    Given a Chart file using the new Helm chart naming convention
    When the Helm chart name change migration executes
    Then the Chart file migration is skipped