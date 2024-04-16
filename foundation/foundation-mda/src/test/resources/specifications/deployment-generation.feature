@deployment-generation
Feature: Generating deployments
  @manual
  Scenario: Customize sub-app values files
    Given An ArgoCD app-of-apps deployment
    And The sub-apps have a values file named "values-ci.yaml"
    And the top level apps file is configured to use "values-ci.yaml"
    When I deploy the app-of-apps deployment to ArgoCD
    Then the sub-apps are deployed using their "values-ci.yaml" file

  @manual
  Scenario: Hive metastore service deployment relies on hive metastore db deployment
    Given a pipeline that requires spark hive metastore
    When the MDA generation is run
    Then the user is notified to add the hive metastore service deployment to tilt
    And the user is notified to add the hive metastore db deployment to tilt
    And the tilt deployment for hive metastore service depends on hive metastore db

  @manual
  Scenario: The spark worker docker image is managed by tilt
    Given a pipeline that is deployed through spark operator
    When the MDA generation is run
    Then the user is notified to add a spark worker image deployment to tilt
    And the tilt deployment will handle building and reloading the spark worker image

  Scenario Outline: Generate a placeholder SealedSecret for ArgoCD
    When the deployment "<profile>" is generated
    Then the placeholder SealedSecret is created

    Examples:
      | profile                               |
      | mlflow-deploy-v2                      |
      | aissemble-spark-infrastructure-deploy |

  @manual
  Scenario Outline: Project specific namespaces
    Given I have a genreated project named "namespace-test"
    When the deployment profile "<profile>" is generated
    Then the generated chart deploys in a namespace of "namespace-test"
    Examples:
      | profile                                     |
      | airflow-kubernetes-v2                       |
      | elasticsearch-kubernetes                    |
      | elasticsearch-kubernetes-v2                 |
      | inference-kubernetes                        |
      | aissemble-inference-kubernetes              |
      | s3local-kubernetes-v2                       |
      | metadata-kubernetes-v2                      |
      | policy-decision-point-kubernetes-v2         |
      | data-access-kubernetes-v2                   |
      | pipeline-invocation-service-v2              |
      | lineage-custom-consumer-kubernetes          |
      | lineage-http-consumer-kubernetes            |
      | spark-operator-kubernetes                   |
      | spark-operator-kubernetes-v2                |
