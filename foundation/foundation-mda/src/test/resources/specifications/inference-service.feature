@manual
Feature: Port assignment for new inference deployments 

    Scenario: 
        Given one or more pipelines of type "machine-learning" 
        And the pipelines have multiple inference steps
        When the MDA generation is run
        Then the system should assign a unique port in the values file for the inference deployment not already in use for each inference deployment
        And the inference deployment should be successfully deployed using the assigned port