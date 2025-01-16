@properties-generation
Feature: Generating properties file resources

  @module-generation
  Scenario:spark properties generation
    Given project called "example"
    And "data-flow" pipeline is using "data-delivery-spark"
    When the profile for "aissemble-spark-infrastructure-deploy-v2" is generated
    Then spark.properties file is generated in "<sparkPropertiesPath>"
    And spark.properties file generated in "main/resources/configurations/base/spark.properties", "metastore.db.username" properties are set to "hive"


    Examples:
      | sparkPropertiesPath	|
      | main/resources/configurations/base/spark.properties		|
      | main/resources/configurations/env/spark.properties		|