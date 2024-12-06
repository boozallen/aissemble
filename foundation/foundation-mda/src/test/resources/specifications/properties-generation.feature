@properties-generation
Feature: Generating properties file resources

  @module-generation
  Scenario:spark-infrastructure properties generation
    Given project called "example"
    And "data-flow" pipeline is using "data-delivery-spark"
    When the profile for "aissemble-spark-infrastructure-deploy-v2" is generated
    Then spark-infrastructure.properties file is generated in "<sparkInfrastructurePropertiesPath>"
    And spark-infrastructure.properties file generated in "main/resources/configurations/base/spark-infrastructure.properties", "metastore.db.username" properties are set to "hive"


    Examples:
      | sparkInfrastructurePropertiesPath	|
      | main/resources/configurations/base/spark-infrastructure.properties		|
      | main/resources/configurations/env/spark-infrastructure.properties		|