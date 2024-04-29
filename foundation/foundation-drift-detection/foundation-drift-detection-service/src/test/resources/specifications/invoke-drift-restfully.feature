@drift @manual
Feature: Drift Detection -> Drift Rest Service
  As a user, I want to be able to invoke drift detection using a rest service

  Scenario: Invoke drift detection using a rest service using multiple drift variables
    Given a policy has been defined for detecting drift
    When I invoke drift specifying the policy using the rest service with multiple drift variables
    Then I receive the results of drift detection
    
  Scenario: Invoke drift detection using a rest service
    Given a policy has been defined for detecting drift
    When I invoke drift specifying the policy using the rest service with a single drift variable
    Then I receive the results of drift detection
