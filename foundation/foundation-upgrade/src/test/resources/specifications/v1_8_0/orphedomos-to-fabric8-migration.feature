Feature: Migrate from using Orphedomos to Fabric8 docker-maven-plugin

  Scenario: Pom file with Orphedomos plugin configuration is migrated to Fabric8 docker-maven-plugin
    Given A pom with Orphedomos in the plugin management
    When The migration executes
    Then The pom is updated to use the Fabric8 docker-maven-plugin
    And Fabric8 is configured properly
    And the previous configuration was removed

  Scenario Outline: Pom file with Orphedomos plugin configuration in a profile is migrated to Fabric8 docker-maven-plugin
    Given A pom with an Orphedomos config in a "<profile>" profile
    When The migration executes
    Then Fabric8 is configured properly in the "<profile>" profile
    And the previous "<profile>" profile configuration was removed
    Examples:
      | profile          |
      | integration-test |
      | ci               |

  Scenario: Pom file with Orphedomos packaging type is migrated to Fabric8 docker-build
    Given A pom with its packaging set to Orphedomos
    When The migration executes
    Then The pom is updated to use packaging type of docker-build

