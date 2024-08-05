@properties
Feature: Encryption

  Scenario: Data can be encrypted and decrypted using AES algorithm
    Given a plain text string
    When the string is encrypted using AES encryption
    Then the encrypted string can be decrypted using AES

