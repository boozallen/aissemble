@chart-url-change-migration
Feature: As an aiSSEMBLE user, I want my aiSSEMBLE Helm charts updated to the latest Helm chart repository URL

  Scenario: Upgrade aiSSEMBLE Helm chart Chart file to use new helm repository URL
    Given a Chart file referencing the older Helm repository URL
    And the old Helm repository URL is provided through the system property
    When the Helm chart url migration executes
    Then the Chart file is updated to use new Helm repository URL

  Scenario: Skipping Helm Repository URL migration if previous URL not provided through system property
    Given a Chart file
    And the old Helm repository URL is not provided through the system property
    When the Helm chart url migration executes
    Then the Chart file is not updated to use new Helm repository URL