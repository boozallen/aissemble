@dataEncryptionPolicy
Feature: Data Encryption -> Data encryption using a policy

    Scenario: Invoke encryption with a policy
        Given a data encryption policy has been persisted to file
        When the policy is loaded
        Then the policy contains encryption information
