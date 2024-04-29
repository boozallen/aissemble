Feature: Test archetype-post-generate script

  Background:
    Given I am creating a project from foundation-archetype
    And the groupId is "org.example"
    And the artifactId is "test-project"
    And the version is "1.0.0-SNAPSHOT"
    And the package is "org.example"
    And the projectGitUrl is "http://github.com/test-project"

  Scenario Outline: package identifiers should not be a Java reserved word
    Given the package is "<packageName>"
    When the project is created from foundation-archetype
    Then the project creation "<result>" for invalid package name
    Examples:
      | packageName               | result   |
      | com.boozallen.project     | succeeds |
      | true                      | fails    |
      | true.tests                | fails    |
      | org.example.public        | fails    |
      | null                      | fails    |
      | org.example.final.project | fails    |
