@lineage_types
Feature: Baseline data lineage type wrappers can be instantiated, and validate their parameters appropriately.

  Scenario: A RunEvent can be instantiated with valid, minimal args
    When a RunEvent is instantiated with minimal valid arguments
    Then all public RunEvent fields will contain appropriate values

  Scenario: A basic Job can be instantiated with valid, minimal args
    When a Job is instantiated with minimal valid arguments
    Then all public Job fields will contain appropriate values

  Scenario: A basic Job is instantiated with an illegal (non-string) name
    When a Job is instantiated with a non-string name
    Then a ValueError will be raised during the construction of the Job

  Scenario: A basic Facet can be instantiated with valid, minimal args
    When a Facet is instantiated with minimal valid arguments
    Then all public Facet fields will contain appropriate values

  Scenario: A basic Facet is instantiated with an illegal schema_url
    When a Facet is instantiated with an illegal schema_url
    Then a ValueError will be raised during the construction of the Facet

  Scenario: A basic Dataset can be instantiated with valid, minimal args
    When a Dataset is instantiated with minimal valid arguments
    Then all public Dataset fields will contain appropriate values

  Scenario: A basic Dataset is instantiated with an illegal (non-string) name
    When a Dataset is instantiated with a non-string name
    Then a ValueError will be raised during the construction of the Dataset

  Scenario: An InputDataset is instantiated with an illegal input_facet
    When an InputDataset is instantiated with an illegal input_facet dict
    Then a ValueError will be raised during the construction of the Dataset

  Scenario: An OutputDataset is instantiated with an illegal output_facet
    When an OutputDataset is instantiated with an illegal output_facet dict
    Then a ValueError will be raised during the construction of the Dataset

  Scenario: A basic Run is instantiated with valid, minimal args
    When a Run is instantiated with minimal valid arguments
    Then all public Run fields will contain appropriate values

  Scenario: A basic Run is instantiated with an illegal run_id
    When a Run is instantiated with a non-UUID run_id
    Then a ValueError will be raised during the construction of the Run

  Scenario: A Lineage object is created with an illegal facet dict value
    When a lineage object is instantiated with a facet dict containing an illegal value
    Then a ValueError will be raised during the construction of the Lineage Object

  Scenario: A Lineage object is created with an illegal facet dict key
    When a lineage object is instantiated with a facet dict containing an illegal key
    Then a ValueError will be raised during the construction of the Lineage Object