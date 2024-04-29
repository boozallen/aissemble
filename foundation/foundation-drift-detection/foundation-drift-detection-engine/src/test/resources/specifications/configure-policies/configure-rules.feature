@driftConfigurations @unit
Feature: Drift Detection -> Configure rules for detecting drift
  As a user, I need to be able to configure rules for detecting drift, including configuring algorithms

  Scenario: Read in a policy with one rule
    Given a policy has been configured with 1 rules
    When the policy is read in
    Then the policy has 1 corresponding rules

  Scenario Outline: Read in a policy with multiple rules
    Given a policy has been configured with <number> rules
    When the policy is read in
    Then the policy has <number> corresponding rules

    Examples: 
      | number |
      |      2 |
      |      4 |
      |      6 |

  Scenario Outline: Find an algorithm for a rule based on class name
    When the configured policies reference an algorithm "<algorithm>" by class name
    Then the algorithm from "<class>" is used

    Examples: 
      | algorithm                                            | class                                                                   |
      | StandardDeviation                                    | com.boozallen.drift.detection.algorithm.StandardDeviationDriftAlgorithm |
      | MyDriftAlgorithm                                     | com.boozallen.drift.detection.algorithm.MyDriftAlgorithm                |
      | com.different.company.algorithm.CustomDriftAlgorithm | com.different.company.algorithm.CustomDriftAlgorithm                    |

  Scenario: Algorithms may be customized via passed in configurations
    Given a policy rule that uses the algorithm "StandardDeviation" with the following configurations:
      | key               | value |
      | mean              | 25.07 |
      | standardDeviation |   3.0 |
    When the policy rule is read in
    Then the configurations are available to the algorithm

  Scenario: Algorithms may be customized to run on a certain target via passed in configurations
    Given a policy rule that uses the algorithm "StandardDeviation" with the following configurations:
      | key               | value |
      | standardDeviation |   2.5 |
      | zScore            |     2 |
    And the policy rule specifies a target of "myColumn"
    When the policy rule is read in
    Then the configurations are available to the algorithm
    And the target is set as "myColumn"

  # This was mainly for testing the reflection part of the methods (in case they're not found),
  # but should be updated when we decide how we want to handle it For Real
  Scenario: Algorithms should ignore customizations that are not recognized
    Given a policy rule that uses an algorithm with the following unrecognized configurations
      | key                | value |
      | aMadeUpSetting     |  50.0 |
      | notRelevantSetting |   1.0 |
    When the policy rule is read in
    Then the unrecognized configurations are ignored
