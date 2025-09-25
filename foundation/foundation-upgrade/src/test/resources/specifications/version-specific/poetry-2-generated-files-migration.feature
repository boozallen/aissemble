Feature: Migrate PyProject TOML files for Poetry 2

  Scenario: Generated file `include` is updated to account for behavior changes.
    Given a pyproject.toml file that uses includes to package Fermenter-generated files
    When the poetry 2 include migration is executed
    Then the configuration is updated to explicitly list the formats