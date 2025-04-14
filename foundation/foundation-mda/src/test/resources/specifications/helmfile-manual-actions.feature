@helmfile
Feature: Helmfile manual actions are displayed to users

  Scenario Outline: Helmfile manual actions are displayed for deployments
    Given a project with name "example-helmfile" exists with a data-delivery pipeline
    When the "<profile>" is generated
    Then the manual action to add the helmfile release is displayed
     And the ArgoCD template is not created

    Examples:
      | profile                                |
      | spark-infrastructure-deploy-v2         |
      | spark-operator-deploy-v2               |
      | inference-deploy-v2                    |
      | mlflow-deploy-v2                       |
      | policy-decision-point-deploy-v2        |
      | configuration-store-deploy-v2          |
      | aissemble-shared-infrastructure-deploy |

  Scenario: Helmfile manual actions are generated for docker-spark-python-pipelines
    Given a project with name "example-helmfile" exists with a data-delivery pipeline
    When the "docker-spark-python-pipelines" is generated
    Then the manual action to add the helmfile release for the pipeline is displayed
