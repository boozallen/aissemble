@readPolicies @unit
Feature: Policy Based Configuration -> Read policies from a json file
  
  As the system, I need to be able to read policies from files in a directory

  Scenario: Read in a policy from a json file
    Given a json file with a policy with multiple rules
    When the policy is loaded from the file
    Then the policy is available for service invocation

  Scenario: Read in policies from multiple json files
    Given multiple json files exist, each with a configured policy
    When the policies are loaded from the files
    Then all the policies from the multiple json files are available for service invocation

  Scenario: Policies without an identifier are not added
    Given a policy has been configured without an identifier
    When the policy is loaded from the file
    Then the policy is not added

  Scenario: Read in multiple policies from a single file
   Given a json file with multiple policies
   When the policies are loaded from the file
   Then all the policies from the file are available for service invocation
