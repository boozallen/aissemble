@singleRule @cdi
Feature: Drift Detection -> Detect drift using a policy that has a single rule
  
  As a user, I can detect drift using a policy has a single rule configured

  Background: 
    Given a policy has been configured with a single rule

  Scenario: Invoke drift detection on a single input
    When drift detection is invoked specifying the policy and a single input
    Then drift detection is run on the single input using the policy specified

  Scenario: Invoke drift detection on a list of inputs
    When drift detection is invoked specifying the policy and a list of inputs
    Then drift detection is run on the list of inputs using the policy specified

  Scenario: Invoke drift detection on a single input using control data
    When drift detection is invoked specifying the policy, a single input, and control data
    Then the control data is used to set the metrics for the algorithm
    And drift detection is run on the single input using the policy specified

  Scenario: Invoke drift detection on a list of inputs using control data
    When drift detection is invoked specifying the policy, a list of inputs, and control data
    Then the control data is used to set the metrics for the algorithm
    And drift detection is run on the list of inputs using the policy specified
