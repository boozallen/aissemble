Feature:  AIOPS references from the extensions-security projects migrations

  Scenario: Update the extensions-security object names in a downstream project
    Given a Java file references an object with the old object name with aiops
    When the 1.8.0 aiops reference extension security migration executes
    Then the objects are updated to aissemble replacing aiops