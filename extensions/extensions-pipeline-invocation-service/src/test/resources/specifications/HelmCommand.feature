@helm
Feature: Helm commands are built to submit spark applications

  Scenario: Helm installations contain appropriate initial arguments.
    When helm install command base arguments are configured
    Then the appropriate release name will be present
    And the appropriate helm command will be present
    And a valid repo url will be specified
    And a valid version will be specified

  Scenario Outline: Helm installations contain appropriate values specifications
    When a helm command is built using the <profile> execution profile
    Then the appropriate values arguments for <profile> will be specified
    Examples:
      | profile |
      | dev     |
      | ci      |
      | prod    |

  Scenario: Accept arbitrary value overrides
    When a helm command is built containing values overrides
    Then the values overrides are present in the resulting argument list