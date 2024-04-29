@versioning
Feature: Versioning

    Scenario: Model Versioning
        Given a model has been trained
        When I version the trained model
        Then the model artifacts are packaged and deployed
