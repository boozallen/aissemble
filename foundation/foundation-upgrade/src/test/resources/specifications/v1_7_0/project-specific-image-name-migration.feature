@image-name-change-migration
Feature: As an aiSSEMBLE user, I want my aiSSEMBLE project specific images updated to the latest naming convention automatically

  Scenario: Upgrade aiSSEMBLE project specific image names
    Given a new downstream maven project "new-downstream-project"
    And a values file with the older project specific image naming convention
    When the project specific image name change migration executes
    Then the values file is updated to use new project specific image naming convention

  Scenario: Skipping image name migration if image does not contain the pattern "boozallen/<project-name>"
    Given a new downstream maven project "new-downstream-project"
    And a values file with a non project specific image
    When the project specific image name change migration executes
    Then the values file project specific image name change migration is skipped