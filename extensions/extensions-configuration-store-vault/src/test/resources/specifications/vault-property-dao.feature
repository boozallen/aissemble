@vault @integration
Feature: Configuration can be stored to Vault

  @config-loader
  Scenario: The ConfigLoader can read and write to the Vault server
    Given set of properties to be loaded
     When the configurations are loaded
      And the properties can be read from the vault server