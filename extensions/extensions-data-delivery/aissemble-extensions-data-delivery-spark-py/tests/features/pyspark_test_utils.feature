@testutils
Feature: SparkSession creation

  Scenario: Create standalone spark session from SparkApplication YAML
    When a configured spark session is requested for unit testing
    Then an appropriately configured session can be retrieved

  Scenario: Create standalone spark session from SparkApplication YAML with no illegal lines
    When a configured spark session with no unsafe line is requested for unit testing
    Then an appropriately configured session can be retrieved
