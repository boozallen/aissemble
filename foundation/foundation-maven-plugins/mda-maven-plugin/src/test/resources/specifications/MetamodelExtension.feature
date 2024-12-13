@MetamodelExtension
Feature: As an aiSSEMBLE user, I want pipeline artifacts derived from MDA models with extended metamodel schemas

  Scenario: Use model instance repository with metamodel extensions
    Given a model instance repository extending foundation-mda
    And a pipeline with metamodel extensions
    When the copy-pipeline-artifacts goal is executed
    Then the extended metamodel is read successfully
