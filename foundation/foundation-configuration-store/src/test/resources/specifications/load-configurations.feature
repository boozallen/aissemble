@config-loader
Feature: Load configurations at specified URI based on environment context

  Scenario: The Configuration Service loads configurations into an empty config store
    Given a base URI indicating a directory housing valid base configurations 
    And an environment-specific URI indicating a directory housing valid environment-specific configurations
    When the configuration service starts
    Then the configurations are loaded into the configuration store
    And the user is notified that the configurations were loaded successfully
    And the configuration service records the that the given configurations were loaded successfully

  Scenario: the Configuration Service skips loading already fully loaded properties
    Given a base URI indicating a directory housing valid base configurations 
    And an environment-specific URI indicating a directory housing valid environment-specific configurations
    And the configuration store has been fully populated with the specified configurations
    When the configuration service starts
    Then the configuration service skips the loading process
    And notifies the user that the configurations were previously loaded

  Scenario: The properties are not distinguishable
    Given URIs pointing to nondistinct configurations
    When the configurations are loaded
    Then an exception is thrown stating configurations are not distinguishable

  @config-service
  Scenario: configuration service returns the configuration value
      Given the configuration service has started
       When requests a configuration property
       Then the property value is returned