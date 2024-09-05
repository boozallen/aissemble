Feature: Migration ArgoCD application template syncPolicy

  Scenario: Migrate an ArgoCD template without syncPolicy configuration
    Given an ArgoCD template doesn't have syncPolicy configuration defined
    When the 1.9.0 ArgoCD template syncPolicy migration executes
    Then the syncPolicy template function is included in the template

  Scenario: An ArgoCD template with syncPolicy configured is not impacted by this migration
    Given an ArgoCD template has the syncPolicy configuration defined
    When the 1.9.0 ArgoCD template syncPolicy migration executes
    Then the template is unchanged

  Scenario: A not ArgoCD application template is not impacted by this migration
      Given a template that is not an ArgoCD application
      When the 1.9.0 ArgoCD template syncPolicy migration executes
      Then the template is unchanged

  Scenario: Migrate ArgoCD template file indent with 4 spaces
      Given an ArgoCD template file has indent with 4 spaces
      When the 1.9.0 ArgoCD template syncPolicy migration executes
      Then the syncPolicy template function is included in the template
       And the application template still has indent with 4 spaces