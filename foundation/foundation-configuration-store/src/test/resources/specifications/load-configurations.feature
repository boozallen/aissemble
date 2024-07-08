@config-loader
Feature: Load configurations at specified URI based on environment context

  @config-store
  Scenario: The Configuration Service loads configurations into an empty config store
    Given URIs pointing to valid base and environment properties
    And URIs pointing to valid base and environment policies
    When the configuration service starts
    Then the configurations are loaded into the configuration store
    And the user is notified that the configurations were loaded successfully
    And the configuration service records the that the given configurations were loaded successfully

  @config-store
  Scenario: the Configuration Service skips loading already fully loaded configurations
    Given URIs pointing to valid base and environment properties
    And URIs pointing to valid base and environment policies
    And the configuration service records the that the given configurations were loaded successfully
    When the configuration service starts again
    Then the configuration service skips the loading process
    And notifies the user that the configurations were previously loaded

# TODO @config-store
  @manual
  Scenario: The ConfigLoader creates rule metadata for each rule in each policy
    Given URIs pointing to valid base and environment properties
    And URIs pointing to valid base and environment policies
    When the configuration service starts
    Then metadata for each rule is loaded into the configuration store

  Scenario: The ConfigLoader loads base and environment properties successfully
    Given URIs pointing to valid base and environment properties
    When the properties are loaded
    Then the ConfigLoader validates the URI and its contents
    And consumes the base properties
    And augments the base with the environment properties

  @config-service
  Scenario: configuration service returns the configuration value
    Given the configuration service has started
    When requests a configuration property
    Then the property value is returned

  Scenario: The ConfigLoader loads base and environment policies successfully
    Given URIs pointing to valid base and environment policies
    When the policies are loaded
    Then the ConfigLoader consumes the base and environment policies
    And the environment policy overrides the base policy

  Scenario Outline: A policy has a undefined attribute
    Given a URI pointing to a policy with a undefined "<attribute>"
    When the policies are loaded
    Then an exception is thrown stating a policy attribute is undefined

    Examples:
    | attribute                     |
    | targets                       |
    | targets-retrieve-url          |
    | rules                         |
    | rules-classname               |
    | regeneration-method           |
    | regeneration-method-classname |

  Scenario: A property has multiple policies
    Given URIs pointing to policies targeting the same property
    When the policies are loaded
    Then an exception is thrown stating a property cannot be targeted by multiple policies

  Scenario: Encrypted properties are decrypted and stored into the config-store
    Given there exists a krausening_password and encrypted properties
    When the configuration service starts
    Then the loaded properties contains the decrypted value
