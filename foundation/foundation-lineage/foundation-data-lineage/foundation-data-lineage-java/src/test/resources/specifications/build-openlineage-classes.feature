@buildOLClasses
Feature: Data lineage wrapper classes can be used to produce equivalent classes from the OpenLineage project

  Scenario: A Facet is used to build an equivalent OpenLineage RunFacet object
    Given a valid Run Facet object
    When the Facet's function to build an OpenLineage RunFacet is called
    Then a valid OpenLineage RunFacet will be created with its fields populated

  Scenario: A Facet is used to build an equivalent OpenLineage JobFacet object
    Given a valid Job Facet object
    When the Facet's function to build an OpenLineage JobFacet is called
    Then a valid OpenLineage JobFacet will be created with its fields populated

  Scenario: A Facet is used to build an equivalent OpenLineage DatasetFacet object
    Given a valid Dataset Facet object
    When the Facet's function to build an OpenLineage DatasetFacet is called
    Then a valid OpenLineage DatasetFacet will be created with its fields populated

  Scenario: A Run is used to build an equivalent OpenLineage Run object
    Given a valid Run object
    When the Run's function to build an OpenLineage object is called
    Then a valid OpenLineage Run will be created with its fields populated

  Scenario: A Job is used to build an equivalent OpenLineage Job object
    Given a valid Job object
    When the Job's function to build an OpenLineage object is called
    Then a valid OpenLineage Job will be created with its fields populated

  Scenario: An Input Dataset is used to build an equivalent OpenLineage Input Dataset object
    Given a valid Input Dataset object named "test_name"
    When the Input Dataset's function to build an OpenLineage object is called
    Then a valid OpenLineage Input Dataset will be created with its fields populated

  Scenario: An Input Dataset with an input facet is used to build an equivalent OpenLineage Input Dataset object with an input facet
    Given a valid Input Dataset object with an input facet
    When the Input Dataset's function to build an OpenLineage object is called with an input facet
    Then a valid OpenLineage Input Dataset will be created with its fields populated including the input facet

  Scenario: An Output Dataset is used to build an equivalent OpenLineage Output Dataset object
    Given a valid Output Dataset object
    When the Output Dataset's function to build an OpenLineage object is called
    Then a valid OpenLineage Output Dataset will be created with its fields populated

  Scenario: An Output Dataset with a defined output facet is used to build an equivalent OpenLineage Output Dataset object with an output facet
    Given a valid Output Dataset object with a defined output facet
    When the Output Dataset's function to build an OpenLineage object is called with a defined output facet
    Then a valid OpenLineage Output Dataset will be created with its fields populated including an output facet

  Scenario: A Run Event is used to build an equivalent OpenLineage Run Event object
    Given a valid Run Event object
    When the Run Event is converted to the OpenLineage format
    Then a valid OpenLineage Run Event will be created with its fields populated

  Scenario: I can configure specific producer values for a job
    Given I have configured the property "data.lineage.myJob.producer" with the value "http://github.com/myJob"
    And a job named "myJob" for a RunEvent
    When the Run Event is converted to the OpenLineage format
    Then the producer value of the event is set to "http://github.com/myJob"

  Scenario: If I do not set a producer value for a job, it will use the default
    Given I have configured the property "data.lineage.producer" with the value "http://github.com/producer"
    And a job named "someOtherJob" for a RunEvent
    When the Run Event is converted to the OpenLineage format
    Then the producer value of the event is set to "http://github.com/producer"

  Scenario: I can configure namespace values for a job
    Given I have configured the property "data.lineage.myJob.namespace" with the value "myNamespace"
    And a job named "myJob" for a RunEvent
    When the Run Event is converted to the OpenLineage format
    Then the Job namespace value of the event is set to "myNamespace"

  Scenario: A job can set and use a default namespace
    Given the property "data.lineage.myJob.namespace" is not set
    And a job named "myJob" has a default namespace "myDefaultNamespace"
    When the Job is translated to an OpenLineage Job
    Then the Job namespace value is set to "myDefaultNamespace"

  Scenario: If I do not set a namespace value for a job, it will use the set legacy namespace
    Given the property "data.lineage.myJob.namespace" is not set
    And I have configured the property "data.lineage.namespace" with the value "legacyNamespace"
    And a valid Job object named "myJob"
    When the Job is translated to an OpenLineage Job
    Then the Job namespace value is set to "legacyNamespace"

  Scenario: If I set no namespace values for a job and no legacy namespace, it will throw an exception
    Given the property "data.lineage.myJob.namespace" is not set
    And the property "data.lineage.namespace" is not set
    And a valid Job object named "myJob"
    When the Job is translated to an OpenLineage Job
    Then an exception is raised

  Scenario: I can configure namespace values for an input dataset
    Given I have configured the property "data.lineage.myInputDataset.namespace" with the value "myDataSource"
    And a valid Input Dataset object named "myInputDataset"
    When the Input Dataset is translated to an OpenLineage Dataset
    Then the namespace value of the Dataset is set to "myDataSource"

  Scenario: If I do not set a namespace value for an Input Dataset, it will use the set legacy namespace
    Given the property "data.lineage.myInputDataset.namespace" is not set
    And I have configured the property "data.lineage.namespace" with the value "legacyNamespace"
    And a valid Input Dataset object named "myInputDataset"
    When the Input Dataset is translated to an OpenLineage Dataset
    Then the namespace value of the Dataset is set to "legacyNamespace"

  Scenario: If I do not set a namespace value for an Input Dataset and no legacy namespace, it will throw an exception
    Given the property "data.lineage.myInputDataset.namespace" is not set
    And the property "data.lineage.namespace" is not set
    When a valid Input Dataset object named "myInputDataset" is declared
    Then an exception is raised