Feature: Migration Pipeline Invocation Service ArgoCD application helm.valueFiles

  Scenario: Migrate a Pipeline Invocation Service application template without helm.valueFiles configuration
    Given a Pipeline Invocation Service template doesn't have helm.valueFiles configuration defined
    When the 1.11.0 Pipeline Invocation Service ArgoCD template migration executes
    Then the helm.valueFiles template function is included in the template

  Scenario: Skip a Pipeline Invocation Service application template that has helm.valueFiles configuration
      Given a Pipeline Invocation Service template has helm.valueFiles configuration defined
      When the 1.11.0 Pipeline Invocation Service ArgoCD template migration executes
      Then the pipeline invocation service template migration is skipped

  Scenario: Migrate a Pipeline Invocation Service application template indent with 4 spaces
      Given a Pipeline Invocation Service template has indent with 4 spaces
      When the 1.11.0 Pipeline Invocation Service ArgoCD template migration executes
      Then the helm.valueFiles template function is included in the template
       And the application template still has indent with 4 spaces