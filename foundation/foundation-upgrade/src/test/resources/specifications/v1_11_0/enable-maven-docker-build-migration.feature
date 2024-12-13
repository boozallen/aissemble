Feature: Docker Module Pom File Enable Docker Build Migration

  Scenario: Migrate a docker module pom file to remove docker build skip configuration
    Given a docker module pom file with fabric8 pluginManagement and skip configuration defined
    When the 1.11.0 enable maven docker build migration executes
    Then the fabric8 pluginManagement skip configuration is removed

  Scenario: Skip a docker module pom file with no fabric8 plugin skip configuration
    Given a docker module pom file with fabric8 pluginManagement but no skip configuration defined
    When the 1.11.0 enable maven docker build migration executes
    Then docker module pom file migration is skipped