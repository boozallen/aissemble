@pipeline
Feature: Specify pipelines to be generated

  Scenario Outline: Create a valid pipeline file
    Given a pipeline described by "<name>", "<package>", "<type>", "<implementation>"
    When pipelines are read
    Then a valid pipeline is available can be looked up by the name "<name>" and "<package>"

    Examples: 
      | name          | package                  | type             | implementation                 |
      | dataDelivery  | com.boozallen.aiops.test | data-flow        | versioned-streaming-spark-java |
      | dataDelivery  | com.boozallen.aiops.test | data-flow        | versioned-streaming-pyspark    |
      | neuralNetwork | com.boozallen.aiops.test | machine-learning | default                        |

  Scenario Outline: Required pipeline data is not provided
    Given a pipeline described by "<name>", "<package>", "<type>", "<implementation>"
    When pipelines are read
    Then the generator throws an exception about invalid metamodel information

    Examples: 
      | name          | package                  | type             | implementation                 |
      |               | com.boozallen.aiops.test | data-flow        | versioned-streaming-spark-java |
      | neuralNetwork |                          | machine-learning | default                        |
      | dataDelivery  | com.boozallen.aiops.test |                  | versioned-streaming-pyspark    |
      | dataDelivery  | com.boozallen.aiops.test | data-flow        |                                |

  Scenario Outline: Multiple Steps can be added to a pipeline
    Given a pipeline with <numberOfSteps> with the name "<name>" and "<package>"
    When pipelines are read
    Then <numberOfSteps> are found in the pipeline when it is looked up by the name "<name>" and "<package>"

    Examples: 
      | name          | package                  | numberOfSteps |
      | dataDelivery  | com.boozallen.aiops.test |             2 |
      | neuralNetwork | com.boozallen.aiops.test |             5 |

  Scenario Outline: Required step model data is not provided
    Given an otherwise valid pipeline with a step name "<name>" and type "<type>"
    When pipelines are read
    Then the generator throws an exception about invalid metamodel information

    Examples: 
      | name          | type      |
      |               | transform |
      | FooIngestStep |           |

  Scenario: Required persist model data is not provided
    Given an otherwise valid pipeline with a step containing an unspecified persist type
    When pipelines are read
    Then the generator throws an exception about invalid metamodel information

  Scenario: Mode can be specified in persist model
    Given a valid pipeline with a step containing a specified persist mode
    When pipelines are read
    Then the pipeline is created with the specified persist mode

  Scenario: Required inbound model data is not provided
    Given an otherwise valid pipeline with a step containing an unspecified inbound type
    When pipelines are read
    Then the generator throws an exception about invalid metamodel information

  Scenario: Required outbound model data is not provided
    Given an otherwise valid pipelinewith a step containing an unspecified outbound type
    When pipelines are read
    Then the generator throws an exception about invalid metamodel information

  Scenario Outline: Required configuration item model data is not provided
    Given an otherwise valid pipeline with a step containing a configuration item with key "<key>" and value "<value>"
    When pipelines are read
    Then the generator throws an exception about invalid metamodel information

    Examples: 
      | key | value |
      |     | blah  |
      | foo |       |

  Scenario: Provenance creation defaults to on
    Given a valid pipeline with provenance configuration
    When pipelines are read
    Then the pipeline is created with the default provenance creation

  Scenario: Provenance creation can be turned off
    Given a valid pipeline with provenance disabled
    When pipelines are read
    Then the pipeline is created without including provenance creation

  Scenario: Alerting is enabled by default for a step
    Given a valid pipeline with a step that has no explicit alerting
    When pipelines are read
    Then the pipeline step is created with alerting enabled

  Scenario: Alerting can be disabled for a step
    Given a valid pipeline with a step that has alerting disabled
    When pipelines are read
    Then the pipeline step is created with alerting disabled

  Scenario: Messaging requires channel name
    When a pipeline is configured for messaging without a channel name
    When pipelines are read
    Then an error is thrown

  Scenario: Pipelines can be distinguished based on type
    Given a valid machine learning pipeline and a valid data flow pipeline
    When data flow artifact ids are requested
    Then only the data flow artifact ids are retrieved

  Scenario: Pipeline steps can be distinguished based on pipeline type
    Given a valid machine learning pipeline and a valid data flow pipeline
    When data flow artifact ids are requested
    Then step artifact ids for data flow are retrieved

  Scenario: Machine learning pipeline can be distinguished based on type
    Given a valid machine learning pipeline and a valid data flow pipeline
    When data flow artifact ids are requested
    Then step artifact ids for machine learning are retrieved

  Scenario: Versioning is enabled by default
    Given a valid machine learning pipeline
    When pipelines are read
    Then the pipeline is created with versioning

  Scenario: Versioning can be disabled
    Given a valid machine learning pipeline with versioning disabled
    When pipelines are read
    Then the pipeline is created without versioning

  Scenario: Platforms can be specified for a pipeline
  	Given platforms are specified for a pipeline
  	When pipelines are read
  	Then the pipeline is created with the specified platforms

  Scenario: Machine learning pipeline can be distinguished based on type
    Given a valid machine learning pipeline and a valid data flow pipeline
    When data flow artifact ids are requested
    Then the machine learning pipeline yields mlflow artifacts
    And the data flow pipeline does not yield mlflow artifacts

  Scenario: Lineage is disabled by default
    Given a valid data delivery pipeline with lineage undefined
    When pipelines are read
    Then the pipeline is created with lineage disabled

  Scenario: Lineage is enabled
    Given a valid data delivery pipeline with lineage enabled
    When pipelines are read
    Then the pipeline is created with lineage enabled

  Scenario: Data profiling is disabled by default
    Given a valid machine learning pipeline with data profiling undefined
    When machine learning pipelines are validated
    Then the pipeline is created without data profiling

  Scenario: Data profiling is set to true 
    Given a valid machine learning pipeline with data profiling set to true
    When machine learning pipelines are validated
    Then an error is thrown

  Scenario: Data profiling is set to false 
    Given a valid machine learning pipeline with data profiling set to false
    When machine learning pipelines are validated
    Then an error is thrown
 
  Scenario: Machine learning pipeline with Training step deosn't yield airflow by default
    Given a valid machine learning pipeline with a training step
    When machine learning artifact ids are requested
    Then the machine learning pipeline does not yield airflow artifacts

  Scenario: Machine learning pipeline with an airflow executionHelper and training step yields airflow 
    Given a valid machine learning training pipeline with an airflow executionHelper
    When machine learning artifact ids are requested
    Then the machine learning pipeline yields airflow artifacts    

  Scenario: Machine learning pipeline with an airflow executionHelper and no training step doesn't yields airflow
    Given a valid machine learning pipeline with an airflow executionHelper and no training step
    When machine learning artifact ids are requested
    Then the machine learning pipeline does not yield airflow artifacts    

  Scenario: Model Lineage is disabled by default
    Given a valid machine learning pipeline with a training step
    When pipelines are read
    Then the training step is created with modelLineage disabled
  
  Scenario: Model Lineage is enabled
    Given a valid machine learning pipeline with a training step and modelLineage is enabled
    When pipelines are read
    Then the training step is created with modelLineage enabled

  Scenario: Data lineage is invalid for an ML pipeline
    Given an otherwise valid machine learning pipeline with data lineage enabled
    When pipelines are read
    Then there are validation error messages

  Scenario: Model lineage is invalid for a data delivery pipeline
    Given an otherwise valid data delivery pipeline with a step that has model lineage defined
    When pipelines are read
    Then there are validation error messages