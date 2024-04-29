Feature: Example record with required string field and validated string field

  @validatedField
  Scenario Outline: Input for a validated string field that requires a minimum length of 5
    Given a string for validated field: "<input>"
    When I update a field that requires a minimum length of 5
    Then I should fail validation: <result>

    Examples:
      | input  | result |
      |        | true   |
      | null   | true   |
      | x      | true   |
      | xxxxx  | false  |

  @requiredField
  Scenario Outline: Input for a required string field
    Given a string for a required field: "<input>"
    When I update a required field
    Then I should fail validation: <result>

    Examples:
      | input  | result |
      |        | true   |
      | null   | true   |
      | x      | false  |