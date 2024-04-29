@pyspark_validation
Feature: Validation

  Scenario Outline: Type coercion for validate
    Given a dictionary type with range validation "<minValue>" and "<maxValue>"
    When validation occurs on a "<value>" typed as a string
    Then validation is "<successful>"

    Examples:
      | minValue | maxValue | value  | successful |
      | 100      | 999      | 100    | true       |
      | 100      | 999      | 999    | true       |
      | 100      | 999      | 0      | false      |
      | 100      | 999      | 1001   | false      |
      | 12.345   | 100.0    | 12.345 | true       |
      | 12.345   | 100.0    | 100.0  | true       |
      | 12.345   | 100.0    | 12     | false      |
      | 12.345   | 100.0    | 100.1  | false      |