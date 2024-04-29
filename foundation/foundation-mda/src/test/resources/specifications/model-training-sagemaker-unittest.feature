@manual
Feature: generate unit test templates for sagemaker model training
    Scenario:
        Given a machine learning pipeline
        And the pipeline has a sagemaker-training step
        When the mda generation engine is run
        Then the sagemaker training unit test templates are generated