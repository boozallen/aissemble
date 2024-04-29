@dataTransform
Feature: Data Transform -> Data transformation using a policy

    Scenario: Invoke transformation using default rule configurations
        Given a data transform policy has been configured
        When the policy is applied on a dataset
        Then the dataset is transformed using the rule specified in the policy
