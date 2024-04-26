@spark-memory-migration
Feature: Migrate a spark application to use the new memory value
  Scenario Outline: Migrate a spark application with new memory value in the base values
    Given a project that has a spark application
    And the base value contains the old memory value
    When the 1.7.0 spark application memory migration executes
    Then the memory is updated to new value

  Scenario: Skip spark application memory migration with non-default values in the base values
     Given a project that has a spark application
     And the base values.yaml does not have default memory values
     When the 1.7.0 spark application memory migration executes
     Then the spark application memory migration is skipped