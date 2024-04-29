@configureStandardDeviation @cdi
Feature: Drift Detection -> Default Standard Deviation Drift Detection
  
  As a user, I can use standard deviations to detect drift on a set of data. The zScore is the number of standard deviations a value is
  from the mean. In many cases, the expected mean and standard deviation are known values, and so can be configured within the policy.
  When drift detection is run against a set of input data using standard score, the configured mean and standard deviations are used to
  see if any values appear outside of the expected number of standard deviations. If so, there is drift within the input data. 
  
  A set of control data can be used to set the mean and standard deviation if the expected values are not known, configured, or need to be overridden.

  Scenario: Configure a mean and standard deviation to be used
    Given a policy with one rule configured to use the standard deviation algorithm
    And the rule specifies a mean and standard deviation
    When drift detection is invoked using the policy
    Then the standard deviation will use the specified mean and standard deviation

  Scenario: Control data overrides any configured mean and standard deviation
    Given a policy with one rule configured to use the standard deviation algorithm
    And the rule specifies a mean and standard deviation
    When drift detection is invoked using control data and the policy
    Then the standard deviation will use the mean and standard deviation calculated from the control data

  Scenario: zScore is set to 1 by default
    When the zScore is not configured by a rule
    Then the zScore is set to 1 by default

  Scenario: The zScore can be configured
    Given a policy with one rule configured to use the standard deviation algorithm
    And the rule specifies a zScore
    When drift detection is invoked using the policy
    Then the standard deviation will use the zScore configured by the rule

  Scenario: If the algorithm hasn't been configured and no control data is passed in, drift is not detected
