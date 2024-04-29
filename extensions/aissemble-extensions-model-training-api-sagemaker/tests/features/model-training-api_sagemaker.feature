@modelTrainingAPISagemaker
Feature: Model Training API

    Scenario: Submit Job to Sagemaker
        Given I can authenticate with AWS SageMaker
        When I submits a job to sagemaker
        Then I get a new job ID returned

    Scenario: Retrieve Job Status from Sagemaker
        Given I can authenticate with AWS SageMaker
        When I try to retrieve job status 
        Then I get the status of the job

    Scenario: Set Authentication for SageMaker Via Environment Variables
        Given I can get aws key from environment variables
        Then I can set authentication for SageMaker via env