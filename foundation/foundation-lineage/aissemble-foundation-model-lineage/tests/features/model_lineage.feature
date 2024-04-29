@model_lineage
Feature: Baseline Lineage Instance can be updated by model facets
  Scenario: Job Instance can be updated by source code facets
    Given a model lineage job
    And a source code facet with a specified type, URL and path
    When the job is updated with the source code facet
    Then the job retains the properties of the source code facet

  Scenario: Run Instance can be updated by Hyperparameter facet
    Given a model lineage run
    And a Hyperparameter facet with a specificied parameter and value
    When the run is updated with the Hyperparameter facet
    Then the run retains the properties of the Hyperparameter facet

  Scenario: Run Instance can be updated by MLflowRunFacet facet
    Given a model lineage run
    And a MLflowRunFacet is defined
    When the run is updated with the MLflowRunFacet
    Then the run retains the properties of the MLflowRunFacet

  Scenario: Run Instance can be updated by HardwareDetailsRunFacet facet
    Given a model lineage run
    And a HardwareDetailsRunFacet is defined
    When the run is updated with the HardwareDetailsRunFacet
    Then the run retains the properties of the HardwareDetailsRunFacet

  Scenario: Run Instance can be updated by PerformanceMetricRunFacet
    Given a model lineage run
    And a PerformanceMetricRunFacet is defined
    When the run is updated with the PerformanceMetricRunFacet
    Then the run retains the properties of the PerformanceMetricRunFacet