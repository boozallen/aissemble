@hashing
Feature: Convert data element to hash

  Scenario Outline: generate a hash for a given data element
    When a data element is provided
    Then a hash is generated

  Scenario Outline: encrypt a given data element and decrypt it using AES
    Given a data element is provided
    When the data element is encrypted
    Then decrypting it will return the original value
