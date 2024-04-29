@data-lineage
Feature: Basic Pipeline Lineage Event Test

  Scenario: Pipeline step lineage is associated with the pipeline lineage
     Given a pipeline run is created
       And the pipeline has the step name Ingest
      When the step lineage run event is created
      Then the step lineage run event's parent run is the pipeline run