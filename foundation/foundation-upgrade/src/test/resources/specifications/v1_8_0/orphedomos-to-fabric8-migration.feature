Feature: Migrate from using Orphedomos to Fabric8 docker-maven-plugin

  Scenario: Pom file with Orphedomos plugin configuration is migrated to Fabric8 docker-maven-plugin
    Given A pom with Orphedomos in the plugin management
    When The migration executes
    Then The pom is updated to use the Fabric8 docker-maven-plugin
    And Fabric8 is configured properly
    And the previous configuration was removed

  Scenario: Pom file with Orphedomos packaging type is migrated to Fabric8 docker-build
    Given A pom with its packaging set to Orphedomos
    When The migration executes
    Then The pom is updated to use packaging type of docker-build

  Scenario: Migration does not run for pom file within <project-name>-tests-docker module
    Given A pom within the tests docker module
    When The migration is executed
    Then The pom is not updated to use the Fabric8 docker-maven-plugin
