Feature: Spark is updated to use appropriate v3.5.0 configs

  Scenario: Update a project with outdated spark configuration
    Given a project with outdated spark application configuration
    When the spark version upgrade migration executes
    Then the spark application configs are updated

  Scenario: A project with up to date spark configuration is not migrated
    Given a project with up to date spark application configuration
    When the spark version upgrade migration executes
    Then the spark application configs are not updated
