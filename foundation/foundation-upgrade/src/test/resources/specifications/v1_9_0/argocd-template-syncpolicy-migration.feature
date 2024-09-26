Feature: Migration ArgoCD application template syncPolicy

  Scenario: Migrate an ArgoCD template without syncPolicy configuration
    Given an ArgoCD template doesn't have syncPolicy configuration defined
    When the 1.9.0 ArgoCD template syncPolicy migration executes
    Then the syncPolicy template function is included in the template

  Scenario: A not ArgoCD application template is not impacted by this migration
      Given a template that is not an ArgoCD application
      When the 1.9.0 ArgoCD template syncPolicy migration executes
      Then the template is unchanged

  Scenario: Migrate ArgoCD template file indent with 4 spaces
      Given an ArgoCD template file has indent with 4 spaces
      When the 1.9.0 ArgoCD template syncPolicy migration executes
      Then the syncPolicy template function is included in the template
       And the application template still has indent with 4 spaces

  Scenario: Migrate an ArgoCD template that has hard-coded sync options
    Given an ArgoCD template that has hard-coded sync options
    When the 1.9.0 ArgoCD template syncPolicy migration executes
    Then a helm function to append syncOptions from Helm values is added to syncPolicy
    And the syncPolicy is updated to set the automated configuration from the values file

  Scenario: Instruct user when the ArgoCD template cannot be migrated
    Given an ArgoCD template with a syncPolicy that does not conform to the 1.8 upgrade workaround
    When the 1.9.0 ArgoCD template syncPolicy migration executes
    Then the migration is reported as unsuccessful
    And instructions for updating the file manually are added to the end of the template as a comment

  Scenario: Skip ArgoCD template that has hard-coded sync options when migrated twice
    Given an ArgoCD template that has hard-coded sync options
    When the 1.9.0 ArgoCD template syncPolicy migration executes a second time
    Then the syncPolicy migration is skipped

  Scenario: Skip ArgoCD template that has no syncPolicy when migrated twice
    Given an ArgoCD template doesn't have syncPolicy configuration defined
    When the 1.9.0 ArgoCD template syncPolicy migration executes a second time
    Then the syncPolicy migration is skipped

  Scenario: Skip unsupported ArgoCD template when migrated twice
    Given an ArgoCD template with a syncPolicy that does not conform to the 1.8 upgrade workaround
    When the 1.9.0 ArgoCD template syncPolicy migration executes a second time
    Then the syncPolicy migration is skipped