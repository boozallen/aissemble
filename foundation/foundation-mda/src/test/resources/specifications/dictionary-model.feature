@dictionary
Feature: Specify dictionary of semantically defined types

  Scenario Outline: Create a valid dictionary file
    Given a dictionary described by "<name>", "<package>"
    When dictionaries are read
    Then a valid dictionary is available can be looked up by the name "<name>" and "<package>"

    Examples: 
      | name                 | package                        |
      | geoTypeDictionary    | com.boozallen.aiops.geospatial |
      | cyberTypeDictionary  | com.boozallen.aiops.cyber      |
      | personTypeDictionary | com.boozallen.aiops.person     |

  Scenario Outline: Create types within a dictionary file
    Given a dictionary with the following types:
      | name        | simpleType        |
      | <type1Name> | <type1SimpleType> |
      | <type2Name> | <type2SimpleType> |
    When dictionaries are read
    Then the dictionary returns the following types:
      | name        | simpleType        |
      | <type1Name> | <type1SimpleType> |
      | <type2Name> | <type2SimpleType> |

    Examples: 
      | type1Name | type1SimpleType | type2Name | type2SimpleType |
      | ssn       | string          | age       | integer         |
      | ipAddress | string          | port      | integer         |

  Scenario Outline: Dictionary Type has valid format criteria
    Given a dictionary with the formats "<formats>"
    When dictionaries are read
    Then a valid dictionary type is available that contains the formats "<formats>"

    Examples: 
      | formats                             |
      | ^[0-9]{5}(?:-[0-9]{4})?$,^[A-Z]{5}$ |
      | ^[0-9]{5}$                          |

  Scenario: Dictionary Type does not try to apply empty string formats
    Given a dictionary type with empty string as a format
    When dictionaries are read
    Then a valid dictionary type is available that does not contains any formats

  Scenario Outline: Dictionary Type has valid length criteria
    Given a dictionary type with length validation <minLength> and <maxLength>
    When dictionaries are read
    Then a valid dictionary type is available that contains the length validations <minLength> and <maxLength>

    Examples: 
      | minLength | maxLength |
      |         1 |         5 |
      |         5 |         5 |

  Scenario Outline: Dictionary Type with a min length specified and NO max length
    Given a dictionary type with min length validation <minLength>
    When dictionaries are read
    Then a valid dictionary type is available that contains the min length <minLength>

    Examples: 
      | minLength |
      |         0 |
      |        23 |

  Scenario Outline: Dictionary Type with a max length specified and NO min length
    Given a dictionary type with max length validation <maxLength>
    When dictionaries are read
    Then a valid dictionary type is available that contains the max length <maxLength>

    Examples: 
      | maxLength |
      |         0 |
      |        23 |

  Scenario: Dictionary Type has tries to specify a minLength greater than the maxLength
    Given a dictionary type with length validation 10 and 8
    When dictionaries are read
    Then a valid dictionary type returns an error around length validation

  Scenario Outline: Dictionary Type has tries to specify a length less than zero
    Given a dictionary type with length validation <minLength> and <maxLength>
    When dictionaries are read
    Then a valid dictionary type returns an error around length validation

    Examples: 
      | minLength | maxLength |
      |         0 |        -1 |
      |      -100 |        10 |

  Scenario Outline: Dictionary Type has valid range criteria
    Given a dictionary type with range validation "<minValue>" and "<maxValue>"
    When dictionaries are read
    Then a valid dictionary type is available that contains the range validations "<minValue>" and "<maxValue>"

    Examples: 
      | minValue | maxValue |
      |        1 |        5 |
      |      5.1 |      5.9 |

  Scenario Outline: Dictionary Type has tries to specify a minValue greater than the maxValue
    Given a dictionary type with range validation "<minValue>" and "<maxValue>"
    When dictionaries are read
    Then a valid dictionary type returns an error around range validation

    Examples: 
      | minValue | maxValue |
      |      100 |       25 |
      |      0.1 |     -2.0 |

  Scenario Outline: Dictionary Type has valid scale
    Given a dictionary type with scale of <scale>
    When dictionaries are read
    Then a valid dictionary type is available that contains the scale <scale>

    Examples: 
      | scale |
      |     0 |
      |     3 |
      |    10 |

  Scenario Outline: Dictionary Type has tries to specify a negative scale value
    Given a dictionary type with scale of <scale>
    When dictionaries are read
    Then a valid dictionary type returns an error around scale validation

    Examples: 
      | scale |
      |    -1 |
      |   -10 |

  Scenario Outline: specify a potection policy within a type
    Given a dictionary type with protection policy URN of "<protectionPolicyUrn>"
    When dictionaries are read
    Then a valid dictionary type is available that contains the protection policy URN "<protectionPolicyUrn>"

    Examples: 
      | protectionPolicyUrn                              |
      | urn:com:boozallen:secrets:maskUnlessOwner        |
      | urn:mil:dod:authorization:encrypt:unlessFiveEyes |

  Scenario Outline: does not try to apply empty protection policies
    Given a dictionary type with an empty protection policy URN
    When dictionaries are read
    Then a valid dictionary type is available that contains no protection policy URN

  Scenario Outline: specify a ethics policy within a type
    Given a dictionary type with ethics policy URN of "<ethicsPolicyUrn>"
    When dictionaries are read
    Then a valid dictionary type is available that contains the ethics policy URN "<ethicsPolicyUrn>"

    Examples: 
      | ethicsPolicyUrn                                     |
      | urn:com:boozallen:ethics:genderDistribution         |
      | urn:gov:agency:bias:priviledgedDemographicBreakdown |

  Scenario: does not try to apply empty ethics policies
    Given a dictionary type with an empty ethics policy URN
    When dictionaries are read
    Then a valid dictionary type is available that contains no ethics policy URN

  Scenario Outline: specify a drift policy within a type
    Given a dictionary type with drift policy URN of "<driftPolicyUrn>"
    When dictionaries are read
    Then a valid dictionary type is available that contains the drift policy URN "<driftPolicyUrn>"

    Examples: 
      | driftPolicyUrn                           |
      | urn:com:boozallen:drift:withinTwoZScores |
      | urn:gov:agency:drift:fancyDriftApproachX |

  Scenario: does not try to apply empty drift policies
    Given a dictionary type with an empty drift policy URN
    When dictionaries are read
    Then a valid dictionary type is available that contains no drift policy URN
