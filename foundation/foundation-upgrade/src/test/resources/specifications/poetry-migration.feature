Feature: As an aiSSEMBLE user, I want my Poetry Python packages updated to the latest naming conventions and versions automatically so upgrade errors are minimized

Scenario Outline: Upgrade aiSSEMBLE Python packages to latest naming convention and version
    Given a pyproject.toml file with old aiSSEMBLE Python dependency naming conventions and versions
    When the pyproject.toml file migration is executed
    Then the dependencies are updated to the newest naming convention and version

Scenario: Upgrade aiSSEMBLE Python packages to latest naming convention and version with partialy upgraded Python packages
    Given a pyproject.toml file with old and new aiSSEMBLE Python dependency naming conventions and versions
    When the pyproject.toml file migration is executed
    Then the dependencies are updated to the newest naming convention and version

Scenario: Skip upgrade 
    Given a pyproject.toml file with new aiSSEMBLE Python dependencies
    When the pyproject.toml file migration is executed
    Then the migration is skipped