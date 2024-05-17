@config-loader
Feature: Load configurations at specified URI based on environment context

  Scenario: The ConfigLoader loads base and environment configurations successfully
    Given URIs pointing to valid base and environment configurations
    When the configuration service starts
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

  @config-service
  Scenario: configuration service returns the configuration value
      Given the configuration service has started
       When requests a configuration property
       Then the property value is returned