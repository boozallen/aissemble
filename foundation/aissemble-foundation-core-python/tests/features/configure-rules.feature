@policyBasedConfigurations @unit
Feature: Policy Configuration -> Configure rules for policies
  As a user, I need to be able to configure rules for policies

  Scenario: Read in a policy with one rule
    Given a policy has been configured with 1 rules
    When the policy is read in
    Then the policy has 1 corresponding rules

  Scenario: Read in a policy with a deprecated target attribute
    Given a policy has been configured with the deprecated target attribute
    When the policy is read in
    Then the policy has 1 corresponding targets

  Scenario Outline: Read in a policy with multiple rules
    Given a policy has been configured with <number> rules
    When the policy is read in
    Then the policy has <number> corresponding rules

    Examples: 
      | number |
      |      2 |
      |      4 |
      |      6 |

  Scenario Outline: Read in a policy with multiple targets
    Given a policy has been configured with <number> targets
    When the policy is read in
    Then the policy has <number> corresponding targets

    Examples: 
      | number |
      |      2 |
      |      4 |
      |      6 |

  Scenario: Rules must have an associated class name
    Given a rule within a policy has been configured without a class name
    When the policy is read in
    Then the rule is ignored

  Scenario: Policies with no valid rules are ignored
    Given a policy has no valid rules
    When the policy is read in
    Then the policy is ignored

  Scenario: Policies may be customized to run on a certain target via configuration
    Given a valid policy exists
    And the policy specifies a target
      | retrieveUrl              | type |
      | http://mydata/lives/here | rest |
    When the policy is read in
    Then the target type is set as "rest"
    And the target's retrieve url is set as "http://mydata/lives/here"
    
  Scenario: Rules may customize the policy targets
    Given a policy exists with the following targets:
      | retrieveUrl         | type  |
      | http://getdata1.com | rest1 |
      | http://getdata2.com | rest2 |
    And a policy rule specifies the target configurations:
      | key          | value      |
      | myQueryParam | myDataName |
      | count        | 10         |
    When the policy is read in
    Then the configured targets are available to the rule

  Scenario: Rules may be customized via passed in configurations
    Given a policy rule that uses the class "MyCalculator" with the following configurations:
      | key               | value |
      | mean              | 25.07 |
      | standardDeviation |   3.0 |
    When the policy rule is read in
    Then the configurations are available to the rule
