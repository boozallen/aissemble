@detectDriftStandardScore @cdi
Feature: Drift Detection -> Detect Drift using Standard Deviations
  A user, I can detect drift using the default standard deviation algorithm

  Background: 
    Given a policy with a single rule configured to use the standard deviation algorithm

  Scenario Outline: No drift is detected when all input values are between the upper and lower bounds
    Given the algorithm has a configured mean of 55, standard deviation of 3, and zScore of 1
    When drift detection is invoked on "<input>" using the policy
    Then input whose values fall outside the upper bound of 58 and lower bound 52 will be flagged for drift
    And drift detected is "<driftDetected>"

    Examples: 
      | input                       | driftDetected |
      |                        52.0 | false         |
      |                    57.99999 | false         |
      | 53.98, 52.009, 56.3, 57.651 | false         |
      |                      51.999 | true          |
      |                     58.0001 | true          |
      | 55, 56.12, 52, 52.14, 51.75 | true          |

  Scenario: Drift will report the value that has been flagged for drift
    When drift is detected on a input
    Then the flagged value will be included in the result
