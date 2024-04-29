@load
Feature: Load configurations at specified URI based on environment context

  Scenario: The ConfigLoader loads base and environment configurations successfully
    Given URIs pointing to valid base and environment configurations
    When the configurations are loaded
    Then the ConfigLoader validates the URI and its contents
    And consumes the base configurations
    And augments the base with the environment configurations

  Scenario: The configurations are misformatted
    Given URIs pointing to misformatted configurations
    When the configurations are loaded
    Then an exception is thrown stating configurations are misformatted

  Scenario: The properties are not distinguishable
    Given URIs pointing to nondistinct configurations
    When the configurations are loaded
    Then an exception is thrown stating configurations are not distinguishable