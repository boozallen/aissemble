@ml-pipeline-generation
Feature: Generation of core machine learning source code modules

    Scenario: Initial generation of ML pipeline modules
       Given a machine-learning pipeline with multiple steps
        When the aissemble-maven-modules profile is generated
        Then the pipeline POM is generated under the pipeline module
         And a POM is generated for each step under the pipeline module
         And the step modules are listed in the pipeline POM

    Scenario: a new step is added to the existing ML pipeline
       Given a machine-learning pipeline
         And the pipeline step modules are already generated
         And a new step is added to the meta model
        When the aissemble-maven-modules profile is generated
        Then a POM is generated for new step under the pipeline module

    Scenario: Manual Action is prompted for missing step module
       Given a machine-learning pipeline with missing step module in POM file
        When the machine-learning-pipeline profile is generated
         And a Manual Action is prompted for adding the step module to the pipeline