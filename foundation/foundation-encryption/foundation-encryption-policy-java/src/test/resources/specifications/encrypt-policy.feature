@encryptPolicies
Feature: Encryption -> Read policies from a json file
  
  As the system, I need to be able to read policies from files in a directory

  Scenario: Read in a policy from a json file
    Given a json file with a policy that contains a list of encrypt fields
    When the policy is loaded from the file
    Then the policy is available for field encryption

  Scenario: By specifying an algorithm in the policy you can dictate which type of encryption is applied
    Given a json file with a policy that specifies an algorithm
    When the policy is loaded from the file
    Then the policy is available with the algorithm
