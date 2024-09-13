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

  Scenario: A POM with the Docker packaging type includes the docker-maven-plugin in the build
    Given a POM with the "docker-build" packaging type
    And the docker-maven-plugin is not already in the build section
    When the add Fabric8 migration executes
    Then the docker-maven-plugin is added to the build section with minimal configuration

  Scenario: The docker plugin is added when there is no existing plugins section
    Given a POM with the "docker-build" packaging type
    And there is no plugins section in build
    When the add Fabric8 migration executes
    Then the docker-maven-plugin is added to the build section with minimal configuration

  Scenario: The docker plugin is added when there is no existing build section
    Given a POM with the "docker-build" packaging type
    And there is no build section
    When the add Fabric8 migration executes
    Then the docker-maven-plugin is added to a new build section with minimal configuration

  Scenario: The migration is skipped if the plugin is already present
    Given a POM with the "docker-build" packaging type
    And the docker plugin is already in the build section
    When the add Fabric8 migration executes
    Then the add Fabric8 migration is skipped

  Scenario Outline: A non-Docker module with the docker-maven-plugin configured is migrated
    Given a POM with the "<packaging>" packaging type
    And the docker-maven-plugin is configured directly in the build
    When the Fabric8 location migration executes
    Then the plugin configuration <is> moved to the pluginManagement section

    Examples:
      | packaging | is     |
      | pom       | is     |
      | jar       | is not |

  Scenario: A non-Docker module with the docker-maven-plugin configured in a profile is migrated
    Given a POM with the "pom" packaging type
    And the docker-maven-plugin is configured directly in the build of a profile
    When the Fabric8 location migration executes
    Then the plugin configuration is moved to the pluginManagement section within the profile

  Scenario: A non-Docker module with the docker-maven-plugin a no other plugins is migrated
    Given a POM with the "pom" packaging type
    And the docker-maven-plugin is the only plugin configured in the build
    When the Fabric8 location migration executes
    Then the plugin configuration is moved to the pluginManagement section and plugins is removed

  Scenario: A non-Docker module with the docker-maven-plugin and no pluginManagement section is migrated
    Given a POM with the "pom" packaging type
    And the docker-maven-plugin is configured directly in the build but there is no pluginManagement section
    When the Fabric8 location migration executes
    Then the plugin configuration is moved to a new pluginManagement section
