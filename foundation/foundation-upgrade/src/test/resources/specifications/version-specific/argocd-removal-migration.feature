Feature: ArgoCD Removal Migration

  Scenario: The project has ArgoCD templates. The migration should remove all the ArgoCD templates
    Given a project has yaml file under templates directory with following properties:
        """
           apiVersion: argoproj.io/v1alpha1
           kind: Application
        """
    And the system property `aissemble.enable.helmfile.migration` is set
    When the ArgoCD removal migration is performed
    Then the ArgoCD yaml is removed
    And the following files are removed in the parent directory:
      | Chart.yaml      |
      | Chart.lock.     |
      | values.yaml.    |
      | values-dev.yaml |
      | values-ci.yaml  |