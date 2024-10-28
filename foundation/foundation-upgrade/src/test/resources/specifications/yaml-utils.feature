Feature: Utilities -> YAML

  Scenario: I can load a YAML file with an object
    Given a YAML file:
       """yaml
       name: test
       size: 7.5
       count: 12
       complete: True
       nested: {
         size: 2.0
       }
       multiple:
         - intvalue: 5
         - intvalue: 7
       """
    When the file is loaded
    Then the string value of the property "name" is "test"
    And the decimal value of the property "size" is 7.5
    And the integer value of the property "count" is 12
    And the boolean value of the property "complete" is "true"
    And the property "nested" is an object
    And the decimal value of the property "size" of the Object "nested" is 2.0
    And the size of the list "multiple" is 2
    And the "intvalue" of item 1 of the list "multiple" is 7

  Scenario: I can load a YAML file with a list
    Given a YAML file:
      """yaml
      - red
      - blue
      - yellow
      """
    When the file is loaded
    Then the size of the list "items" is 3