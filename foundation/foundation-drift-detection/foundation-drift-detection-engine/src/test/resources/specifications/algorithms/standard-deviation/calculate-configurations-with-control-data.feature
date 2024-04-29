@calculateStandardDeviation 
Feature: Drift Detection -> Standard Deviation can be Calculated on Control Data
  
  As the system, I can calculate the mean and standard deviation of a set of control data. Often the expected mean and standard deviation
  are known values. In cases when the mean and standard deviation either aren't known, aren't configured, or need to be overridden, then
  a set of control data can be used to calculate the mean and standard deviation that will be used to detect drift on the input data.

  Scenario Outline: As the system, I can calculate the mean on a set of data
    Given a set of control data "<control>"
    When the mean is calculated
    Then the mean is <mean>

    Examples: 
      | control         | mean |
      | 45, 48, 52, 57  | 50.5 |
      | -4, -2, 0, 1, 3 | -0.4 |
      | 0.25, 2, 0.75   |  1.0 |

  Scenario Outline: As the system, I can calculate the standard deviation within a set of data
    Given a set of control data "<control>"
    And a calculated mean of <mean>
    When the standard deviation is calculated
    Then the standard deviation is <standardDeviation>

    Examples: 
      | control                        | mean      | standardDeviation |
      | 3, 4, 3, 2, 3, 4               |  3.166667 |          0.752773 |
      | 2, 1, 55, 56, 57, 98, 99       | 52.571429 |         39.736034 |
      | -10.1, -8.99, -1.02, 1.78, 3.2 |    -3.026 |          6.154176 |

  Scenario Outline: By default, calculated numbers are rounded to the last 6 digits using the HALF EVEN rounding method
    When a calculated number <number> is rounded
    Then the number is rounded <roundedNumber> using the HALF EVEN rounding method and last 6 digits

    Examples: 
      | number        | roundedNumber |
      |    3.14159265 |      3.141593 |
      | 1.66666666667 |      1.666667 |
      | 9219.65434521 |   9219.654345 |

  # Not for this ticket. Could also configure mean, standard deviation, upper bound, lower bound, etc separately
  Scenario: As the user, I can configure the rounding method.
