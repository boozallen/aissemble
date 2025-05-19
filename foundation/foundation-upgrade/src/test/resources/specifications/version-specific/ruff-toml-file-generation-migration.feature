@helmfile-generate
Feature: Helmfile generation migration is performed correctly and only when needed

  Scenario: The project does not have a root ruff.toml file
    Given a projects does not have a root ruff.toml file
    When the ruff.toml generation migration is performed
    Then the ruff.toml is generated
