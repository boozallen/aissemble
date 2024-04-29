@nametranslation
Feature: Metamodel name translations

  This feature describes how metamodel names are translated to different formats based on their intended usage.

  Scenario Outline: a pipeline name is used as a Python package
    Given a pipeline named "<pipeline>"
    When the names are translated to Python package format
    Then the translated pipeline name is "<output>"
    Examples:
      | pipeline       | output           |
      | UsgsPipeline   | usgs_pipeline    |
      | USGSPipeline   | usgs_pipeline    |
      | My123Pipeline  | my123_pipeline   |
      | MyTESTPipeline | my_test_pipeline |
      | MyPipelineTEST | my_pipeline_test |

  Scenario Outline: a pipeline and step name are used as a Python class
    Given a pipeline named "<pipeline>" and a step named "<step>"
    When the names are translated to Python format
    Then the translated pipeline class name is "<pipelineClass>"
    And the translated step class name is "<stepClass>"
    Examples:
      | pipeline      | step      | pipelineClass | stepClass |
      | UsgsPipeline  | TestStep  | UsgsPipeline  | TestStep  |
      | USGSPipeline  | TESTStep  | USGSPipeline  | TESTStep  |
      | My123Pipeline | My345Step | My123Pipeline | My345Step |

  Scenario Outline: a pipeline and step name are used as a Java class
    Given a spark pipeline named "<pipeline>" and a step named "<step>"
    When the names are translated to Java class format
    Then the translated pipeline class name is "<pipelineClass>"
    And the translated step class name is "<stepClass>"
    Examples:
      | pipeline      | step      | pipelineClass | stepClass |
      | UsgsPipeline  | TestStep  | UsgsPipeline  | TestStep  |
      | USGSPipeline  | TESTStep  | USGSPipeline  | TESTStep  |
      | My123Pipeline | My345Step | My123Pipeline | My345Step |

  Scenario Outline: a pipeline and step name are used as a Maven artifact ID
    Given a pipeline named "<pipeline>" and a step named "<step>"
    When the names are translated to Maven artifact ID format
    Then the translated maven pipeline name is "<pipelineClass>"
    And the maven translated step name is "<stepClass>"
    Examples:
      | pipeline      | step      | pipelineClass   | stepClass   |
      | UsgsPipeline  | TestStep  | usgs-pipeline   | test-step   |
      | USGSPipeline  | TESTStep  | usgs-pipeline   | test-step   |
      | My123Pipeline | My345Step | my123-pipeline  | my345-step  |