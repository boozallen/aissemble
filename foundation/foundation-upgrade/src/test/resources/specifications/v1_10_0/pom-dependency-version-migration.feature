Feature: Update the pom dependencies previously managed by the aiSSEMBLE BOM to include the necessary version

  Scenario: Migrate all pom dependencies to the new version
    Given a POM that references dependencies previously managed by the aiSSEMBLE BOM
    When the POM Dependency Version migration executes
    Then the dependencies are updated to use the necessary version
  
  
  Scenario: Migrate pom dependencies in profiles and dependency management
    Given a POM that references dependencies previously managed by the aiSSEMBLE BOM
    And the POM has the dependencies in profiles and dependency management
    When the POM Dependency Version migration executes
    Then the dependencies are updated to use the necessary version


  Scenario: Skip migration when all the dependencies are using the updated version
    Given a POM that references dependencies previously managed by the aiSSEMBLE BOM
    And the dependencies are all using the updated version
    When the POM Dependency Version migration executes
    Then the POM Dependency Version migration is skipped

  Scenario: Skip migration when the pom does not contain any dependencies that need updating
    Given a POM that does not contain any dependencies previously managed by the aiSSEMBLE BOM
    When the POM Dependency Version migration executes
    Then the POM Dependency Version migration is skipped