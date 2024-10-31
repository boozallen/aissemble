Feature: Updates all references to the quarkus-bom and quarkus-universe-bom to use the new aissemble-quarkus-bom for managing Quarkus dependencies

  Scenario Outline: Migrate Quarkus BOMs to aiSSEMBLE Quarkus BOM
    Given a POM that references the "<bom-artifact-id>" Quarkus BOM
    When the Quarkus Bom Migration executes
    Then the Quarkus BOM is updated to the aiSSEMBLE Quarkus BOM

    Examples:
      | bom-artifact-id      |
      | quarkus-bom          |
      | quarkus-universe-bom |

  Scenario: Skip migration when the Quarkus BOMs are not present
    Given a POM that does not contain a Quarkus BOM
    When the Quarkus Bom Migration executes
    Then the Quarkus Bom Migration is skipped