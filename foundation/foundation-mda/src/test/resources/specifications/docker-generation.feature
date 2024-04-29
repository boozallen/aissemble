@docker
Feature: Generating a docker module

  Scenario Outline: A sensible default producer value is generated
    Given a project named "<projectName>" with a source code url of "github.test.com/myProject"
    And a <type> pipeline using "<implementation>" with <lineage> enabled
    When the docker profile "<profile>" is generated
    Then my lineage property "data.lineage.producer" is set to "github.test.com/myProject"
    Examples:
      | type             | implementation          | lineage       | projectName          | profile                       |
      | data-flow        | data-delivery-spark     | data-lineage  | docker-test-spark    | aissemble-spark-worker-docker |
      | data-flow        | data-delivery-pyspark   | data-lineage  | docker-test-pyspark  | aissemble-spark-worker-docker |
      | machine-learning | machine-learning-mlflow | model-lineage | docker-test-training | aissemble-training-docker     |
