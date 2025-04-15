@helmfile-generate
Feature: Helmfile generation migration is performed correctly and only when needed

  Scenario: The project does not have a helmfile. The migration should create it when the activation key is set
    Given a projects does not have a helm file
    And the system property `aissemble.enable.helmfile.migration` is set
    When the helmfile generation migration is performed
    Then the helmfile is generated

  Scenario: The project has a helmfile. The migration should not create it and when the activation key is set
    Given a projects has a helm file
    And the system property `aissemble.enable.helmfile.migration` is set
    When the helmfile generation migration is performed
    Then the helmfile was not changed

  Scenario: The migration should not run when the activation key is not set
    Given the system property `aissemble.enable.helmfile.migration` is not set
    When the helmfile generation migration is performed
    Then the helmfile generation is skipped
