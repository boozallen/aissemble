@calculateBounds @unit
Feature: Drift Detection -> Calculate Upper and Lower Bounds for Detecting Drift
  
  Once the mean, standard deviation, and standard score/zScore are known, the upper and lower bounds for the range of
  acceptable values can be calculated. Values that are greater than the upper bound or less than than the lower bound
  would be flagged for drift.

  Scenario Outline: Calculate the bounds for detecting drift
    Given a mean of <mean>, a standard deviation of <standardDeviation>, and a zScore <zScore>
    When the bounds for drift detection are calculated
    Then the upper bound is determined to be <upperBound>
    And the lower bound is determined to be <lowerBound>

    Examples: 
      | mean | standardDeviation | zScore | upperBound | lowerBound |
      |   30 |                 2 |      1 |         32 |         28 |
      | 0.05 |              0.01 |      2 |       0.07 |       0.03 |
      |    1 |                 2 |      1 |          3 |         -1 |
      |    0 |                 4 |    1.5 |          6 |         -6 |
