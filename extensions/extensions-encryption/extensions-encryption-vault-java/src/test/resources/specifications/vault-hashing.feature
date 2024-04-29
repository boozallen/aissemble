@integration @vaulthashing
Feature: Convert data element to hash

  Scenario Outline: encrypt a given data element and decrypt it using Hashicorp Vault
    Given a data element is provided
    When the data element is encrypted with vault
    Then decrypting with vault it will return the original value
