@detection
Feature: SparkApplication values files can be appropriately detected and indexed

  Scenario: Values files are detected automatically at startup
    When the application initializes
    And spark application values files are present for detection
    Then the values index will be populated

  Scenario: Values files are detected correctly
    When spark application values files are present for detection
    Then each present spark application will be detected
    And each spark application's classifier set will be detected