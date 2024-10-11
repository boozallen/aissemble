Feature: Migrate Delta Lake Spark dependencies
  The existing Delta Lake jars for accessing delta-lake tables via Spark are not compatible with Spark 3.5.  We have
  updated them from version 2.4.0 to 3.2.1.  In order to upgrade beyond 2.4.0, the delta-core dependency has to be
  migrated to delta-spark (just a straight rename starting at v3).

  Scenario: spark-application chart value files are migrated
    Given a spark-application values file that references delta-core/delta-storage in sparkApp.spec.deps.jars
    When the 1.10.0 DeltaSpark yaml migration executes
    Then the delta-lake jar coordinates are updated to 3.2.1

  Scenario: SparkApplication files are migrated
    Given a SparkApplication file that references delta-core/delta-storage in spec.deps.jars
    When the 1.10.0 DeltaSpark yaml migration executes
    Then the delta-lake jar coordinates are updated to 3.2.1

  Scenario: POM dependencies are migrated
    Given a POM that references the delta-core package
    When the 1.10.0 DeltaSpark POM migration executes
    Then delta-core is updated to delta-spark and the version is set to the version.delta property
