@helmfile
Feature: Migrate helm files from the templates folder to the apps/common-infrastructure folder

  Scenario: Helm files from the templates folder are migrated to the apps/common-infrastructure folder
    Given a project has files in the templates folder
    When the helm templates migration is performed
    Then the yaml files are moved to apps/common-infrastructure/templates

  Scenario: Helm files from the root folder are migrated to the apps/common-infrastructure folder
    Given a project has files in the root deploy folder
    When the root deploy files migration is performed
    Then the yaml files are moved to apps/common-infrastructure
