@properties
Feature: Encryption

  @integration
  Scenario: Data can be encrypted and decrypted using Vault
    Given a plain text string
    When the string is encrypted using Vault
    Then the encrypted string can be decrypted using Vault

  Scenario: Data can be encrypted and decrypted using AES algorithm
    Given a plain text string
    When the string is encrypted using AES encryption
    Then the encrypted string can be decrypted using AES

  @integration
  Scenario: Vault encryption locally by downloading a key from the server
    Given a plain text string
    When local vault encryption is requested
    Then a key is downloaded from the Vault server

  @integration
  Scenario: Local Vault encryption and decryption
    Given a plain text string
    When local vault encryption is requested
    Then the encrypted data can be decrypted using the local key copy

  @integration
  Scenario: Data can be encrypted and decrypted using AES GCM 96 algorithm
    Given a plain text string
    When the string is encrypted using AES GCM 96 encryption
    Then the encrypted string can be decrypted using AES GCM 96

