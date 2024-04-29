@to_openlineage
Feature: Baseline data lineage type wrappers can be converted to equivalent openlineage types.

  Scenario: A Run is converted to an OpenLineage Run
    Given a valid Run object
    When the Run is translated to an OpenLineage Run
    Then the resulting OpenLineage Run will retain all relevant properties

  Scenario: A Job is converted to an OpenLineage Job
    Given a valid Job object named "test_job"
    When the Job is translated to an OpenLineage Job
    Then the resulting OpenLineage Job will retain all relevant properties

  Scenario: A Dataset is converted to an OpenLineage Dataset
    Given a valid Dataset object named "test_dataset"
    When the Dataset is translated to an OpenLineage Dataset
    Then the resulting OpenLineage Dataset will retain all relevant properties

  Scenario: An InputDataset is converted to an OpenLineage InputDataset
    Given a valid InputDataset object named "test_InputDataset"
    When the InputDataset is translated to an OpenLineage InputDataset
    Then the resulting OpenLineage InputDataset will retain all relevant properties

  Scenario: An OutputDataset is converted to an OpenLineage OutputDataset
    Given a valid OutputDataset object named "test_OutputDataset"
    When the OutputDataset is translated to an OpenLineage OutputDataset
    Then the resulting OpenLineage OutputDataset will retain all relevant properties

  Scenario: A RunEvent is converted to an OpenLineage RunEvent
    Given a valid RunEvent object
    And the RunEvent has an InputDataset named "test_InputDataset"
    And the RunEvent has an OutputDataset named "test_OutputDataset"
    And the RunEvent has an OutputDataset named "test_OutputDataset2"
    When the RunEvent is translated to an OpenLineage RunEvent
    Then the resulting OpenLineage RunEvent will retain all relevant properties

  Scenario: A Facet is converted to an OpenLineage Facet
    Given a valid Facet object
    When the Facet is translated to an OpenLineage Facet
    Then the resulting OpenLineage Facet will retain all relevant properties

  Scenario: I can configure specific producer values for a job
    Given I have configured the property "data.lineage.myJob.producer" with the value "http://github.com/myJob"
    And a job named "myJob" for a RunEvent
    When the RunEvent is translated to an OpenLineage RunEvent
    Then the producer value of the event is set to "http://github.com/myJob"

  Scenario: If I do not set a producer value for a job, it will use the default
    Given I have configured the property "data.lineage.producer" with the value "http://github.com/producer"
    And a job named "someOtherJob" for a RunEvent
    When the RunEvent is translated to an OpenLineage RunEvent
    Then the producer value of the event is set to "http://github.com/producer"
#
  Scenario: I can configure namespace values for a job
    Given I have configured the property "data.lineage.myJob.namespace" with the value "myNamespace"
    And a job named "myJob" for a RunEvent
    When the RunEvent is translated to an OpenLineage RunEvent
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

  Scenario: If I do not set a namespace value for a Dataset, it will use the set legacy namespace
    Given the property "data.lineage.myDataset.namespace" is not set
    And I have configured the property "data.lineage.namespace" with the value "legacyNamespace"
    And a valid Dataset object named "myDataset"
    When the Dataset is translated to an OpenLineage Dataset
    Then the namespace value of the Dataset is set to "legacyNamespace"

  Scenario: If I do not set a namespace value for a Dataset and no legacy namespace, it will throw an exception
    Given the property "data.lineage.myDataset.namespace" is not set
    And the property "data.lineage.namespace" is not set
    And a valid Dataset object named "myDataset"
    When the Dataset is translated to an OpenLineage Dataset
    Then an exception is raised