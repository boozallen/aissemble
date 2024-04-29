@configure
Feature: Failure behavior can be configured

  Scenario: Failure behavior is overridden for a specific application
    When an application is configured to override the default failure behavior
    Then the correct behavior pattern will be identified for use

  Scenario: Default failure behavior is accepted when application-specific overrides are not present
    When an application is not configured to override the default failure behavior
    Then the correct behavior pattern will be identified for use