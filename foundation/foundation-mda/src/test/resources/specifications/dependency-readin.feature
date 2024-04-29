@dependencies
Feature: Dependency versions can be read in from maven properties.
  Scenario: Dependency versions specified as pom properties can be accessed programmatically.
    Given the properties file has been properly filtered
    When the spark dependency configuration is initialized
    Then appropriate version information will be available
