@chart-module-change-migration
Feature: As an aiSSEMBLE user, I want my aiSSEMBLE Helm module names updated to the latest name automatically

  Scenario: Upgrade aiSSEMBLE Helm chart value file to use to latest module name
    Given a value file using the older Helm chart module name
    When the Helm module name change migration executes
    Then the value file is migrated to use new module name

  Scenario: Upgrade aiSSEMBLE Helm chart Chart file to use to latest module name
    Given a Chart file using the older Helm chart module name
    When the Helm module name change migration executes
    Then the Chart file is migrated to use new module name

  Scenario: Skipping Value file migration if values file already uses new module name
    Given a value file using the new Helm chart module naming convention
    When the Helm module name change migration executes
    Then the value file module name migration is skipped

  Scenario: Skipping Chart file migration if Chart file already uses new module name
    Given a Chart file using the new Helm chart module naming convention
    When the Helm module name change migration executes
    Then the Chart file module name migration is skipped

