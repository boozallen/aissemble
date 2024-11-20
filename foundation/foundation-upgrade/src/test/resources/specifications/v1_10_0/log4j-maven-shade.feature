Feature: Migrate a Maven Shade Plugin to use the updated Log4j dependency

  Scenario: A Maven Shade Plugin instance is migrated to use the updated dependency
    Given a Maven Shade Plugin instance with an outdated dependency on Log4j
    When the Log4j Maven Shade Plugin migration executes
    Then the Maven Shade Plugin is updated to use the new Log4j dependency
    And the transformer configuration is updated with the new implementation

  Scenario: A Maven Shade Plugin instance without the dependency is not migrated
    Given a Maven Shade Plugin instance without a Log4j dependency
    When the Log4j Maven Shade Plugin migration executes
    Then the Log4j Maven Shade Plugin migration is skipped