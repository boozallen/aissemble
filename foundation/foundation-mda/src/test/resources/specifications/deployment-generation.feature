@deployment-generation
Feature: Generating deployments

  @manual
  Scenario: Hive metastore service deployment relies on hive metastore db deployment
    Given a pipeline that requires spark hive metastore
    When the MDA generation is run
    Then the user is notified to add the hive metastore service deployment to tilt
    And the user is notified to add the hive metastore db deployment to tilt
    And the tilt deployment for hive metastore service depends on hive metastore db
