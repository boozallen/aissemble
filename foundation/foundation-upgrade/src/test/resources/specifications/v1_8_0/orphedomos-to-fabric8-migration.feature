Feature: Migrate from using Orphedomos to Fabric8 docker-maven-plugin

  Scenario: A standard Docker parent POM is migrated to Fabric8
    Given a the default POM for the parent module of a project's Docker moduels
    When the Fabric8 migration executes
    Then the Orphedomos plugin is replaced with Fabric8
    And the "ci" profile configuration is updated

  Scenario: A standard Docker module POM is migrated to Fabric8
    Given a default POM for a Docker module
    When the Fabric8 migration executes
    Then the Orphedomos packaging is replaced with Fabric8's docker-build packaging

  Scenario: Pom file with Orphedomos plugin configuration in profile(s) plugin management section is migrated to Fabric8 docker-maven-plugin
    Given a POM that uses Orphedomos in multiple profiles
    When the Fabric8 migration executes
    Then the Orphedomos plugin is replaced with Fabric8 in all profiles
    And the "ci" profile configuration is updated
    And the "integration-test" profile configuration is updated
    And the "other-profile" profile configuration is updated

  Scenario: A non-applicable POM is not migrated
    Given a POM that does not use the Orphedomos plugin
    When the Fabric8 migration executes
    And the POM is not modified

