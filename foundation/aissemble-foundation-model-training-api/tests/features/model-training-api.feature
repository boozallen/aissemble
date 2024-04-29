@modelTrainingAPI
Feature: Model Training API

    Scenario: Trigger Model Training
        Given I have Kubernetes cluster access
        When I trigger a training job
        Then I get a valid model training job name returned

    Scenario: Get Job Logs
        Given I have Kubernetes cluster access
        When I request job logs
        Then I get logs returned

    Scenario: List Training Jobs
        Given I have Kubernetes cluster access
        When I request a list of training jobs
        Then I get a list of training jobs returned

    Scenario: Kill Training Job
        Given I have Kubernetes cluster access
        When I kill a training job
        Then I get a message indicating the job has been deleted