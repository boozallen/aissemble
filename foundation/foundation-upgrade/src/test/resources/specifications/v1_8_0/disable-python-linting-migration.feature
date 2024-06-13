Feature: Python linting is disabled for projects upgrading to 1.8.0

  Scenario: A standard 1.7.0 project
    Given A root pom without Habushu in the plugin management
    When The 1.8.0 python linting migration executes
    Then The root pom is updated with Habushu in plugin management
    And Habushu is configured with sourceFailOnLintErrors and testFailOnLintErrors disabled

  Scenario: Missing configuration section
    Given A root pom with Habushu in the plugin management and does not contain the configuration tag
    When The 1.8.0 python linting migration executes
    Then The root pom is updated with the configuration tag
    And Habushu is configured with sourceFailOnLintErrors and testFailOnLintErrors disabled

  Scenario: Missing both sourceFailOnLintErrors and testFailOnLintErrors
    Given A root pom with Habushu in the plugin management and does not contain both sourceFailOnLintErrors and testFailOnLintErrors
    When The 1.8.0 python linting migration executes
    Then Habushu is configured with sourceFailOnLintErrors and testFailOnLintErrors disabled

  Scenario: sourceFailOnLintErrors and/or testFailOnLintErrors is already set
    Given A root pom with Habushu in the plugin management and contains either sourceFailOnLintErrors or testFailOnLintErrors
    When The 1.8.0 python linting migration executes
    Then The 1.8.0 python linting migration is skipped
