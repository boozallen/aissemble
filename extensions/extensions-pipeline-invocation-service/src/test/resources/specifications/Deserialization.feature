@deserialize
Feature: Raw input can be deserialized

  Scenario: A string is transformed to a deserialized object
    When an appropriately formatted string is provided
    Then the string can be deserialized to an object
