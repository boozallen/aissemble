@record
Feature: Specify record of semantically defined types

  Background: 
    Given the following dictionary types:
      | name            | simpleType | protectionPolicy | driftPolicy      | ethicsPolicy             |
      | ssn             | string     | hashValuePolicy  |                  |                          |
      | phoneNumber     | string     | hashValuePolicy  |                  |                          |
      | singleSlaInDays | decimal    |                  | oneZScorePolicy  |                          |
      | doubleSlaInDays | decimal    |                  | twoZScoresPolicy |                          |
      | archivable      | boolean    |                  |                  | sampleMinimumPolicy      |
      | gender          | string     |                  |                  | genderDistributionPolicy |
      | binarydata      | byte[]     |                  |                  |                          |

  Scenario Outline: Create a valid record file
    Given a record described by "<name>", "<package>"
    When records are read
    Then a valid record is available can be looked up by the name "<name>" and "<package>"

    Examples: 
      | name                | package                        |
      | FinancialTransction | com.boozallen.aiops.finanicial |
      | NetFlowRecord       | com.boozallen.aiops.cyber      |
      | UserProfile         | com.boozallen.aiops.person     |

  Scenario Outline: A record can have a description
    Given a record with a description "<description>"
    When records are read
    Then a valid record is available with a description of "<description>"

    Examples:
     | description                            |
     | This is a description.                 |
     | This record represents a User Profile. |

  Scenario Outline: A record can contain multiple fields
    Given a record with the fields named:
      | <fieldA> |
      | <fieldB> |
    When records are read
    Then a valid record is available with fields named:
      | <fieldA> |
      | <fieldB> |

    Examples: 
      | fieldA    | fieldB   |
      | firstName | lastName |
      | city      | state    |
      | cidr      | ip       |

  Scenario Outline: A record field can have a description
    Given a record with a field that has a description "<description>"
    When records are read
    Then the record field is available and has a description of "<description>"

    Examples:
      | description                                     |
      | This is a record field description.             |
      | This record field represents the SSN of a user. |

  Scenario Outline: A record can have a field with any valid dictionary type
    Given a record with a field that has dictionary type named "<dictionaryTypeName>"
    When records are read
    Then the record field is available with a simple type of "<expectedSimpleTypeFromBackground>"

    Examples: 
      | dictionaryTypeName | expectedSimpleTypeFromBackground |
      | ssn                | string                           |
      | singleSlaInDays    | decimal                          |
      | archivable         | boolean                          |
      | binarydata         | byte[]                           |

  Scenario Outline: A record field can specify a column that will be used for persistence
    Given a record with a field specifying the column name "<columnName>"
    When records are read
    Then the record field is available with a column name of "<columnName>"

    Examples: 
      | columnName          |
      | ssn                 |
      | total_amount        |
      | field_is_archivable |

  Scenario: A record field can specify if it is required
    Given a record with a field specifying the column is required
    When records are read
    Then the record field is available and marked as required

  Scenario: A record field can specify if it is optional
    Given a record with a field specifying the column as optional
    When records are read
    Then the record field is available and marked as optional

  Scenario Outline: A record field inherits the protection policy of the dictionary type it references
    Given a record with a field that has dictionary type named "<dictionaryTypeFromBackground>"
    When records are read
    Then the record field is available and has a protection policy of "<expectedProtectionPolicyFromBackground>"

    Examples: 
      | dictionaryTypeFromBackground | expectedProtectionPolicyFromBackground |
      | ssn                          | hashValuePolicy                        |
      | phoneNumber                  | hashValuePolicy                        |

  Scenario Outline: A record field inherits the empty protection policy of the dictionary type it references
    Given a record with a field that has dictionary type named "<dictionaryTypeFromBackground>"
    When records are read
    Then the record field is available and has a no protection policy specified

    Examples: 
      | dictionaryTypeFromBackground |
      | singleSlaInDays              |
      | archivable                   |

  Scenario Outline: A record field overrides the protection policy of the dictionary type it references with a new value
    Given a record with a field that has dictionary type named "<dictionaryTypeFromBackground>" and a protection policy of "<overrideProtectionPolicy>"
    When records are read
    Then the record field is available and has a protection policy of "<overrideProtectionPolicy>"

    Examples: 
      | dictionaryTypeFromBackground | overrideProtectionPolicy      |
      | ssn                          | hashAllButLastFourOfSsnPolicy |
      | phoneNumber                  | noOpPolicy                    |

  Scenario Outline: A record field inherits the ethics policy of the dictionary type it references
    Given a record with a field that has dictionary type named "<dictionaryTypeFromBackground>"
    When records are read
    Then the record field is available and has a ethics policy of "<expectedEthicsPolicyFromBackground>"

    Examples: 
      | dictionaryTypeFromBackground | expectedEthicsPolicyFromBackground |
      | archivable                   | sampleMinimumPolicy                |
      | gender                       | genderDistributionPolicy           |

  Scenario Outline: A record field inherits the empty ethics policy of the dictionary type it references
    Given a record with a field that has dictionary type named "<dictionaryTypeFromBackground>"
    When records are read
    Then the record field is available and has a no ethics policy specified

    Examples: 
      | dictionaryTypeFromBackground |
      | ssn                          |
      | phoneNumber                  |

  Scenario Outline: A record field overrides the ethics policy of the dictionary type it references with a new value
    Given a record with a field that has dictionary type named "<dictionaryTypeFromBackground>" and a ethics policy of "<overrideEthicsPolicy>"
    When records are read
    Then the record field is available and has a ethics policy of "<overrideEthicsPolicy>"

    Examples: 
      | dictionaryTypeFromBackground | overrideEthicsPolicy            |
      | archivable                   | archiveOlderThanSixMonthsPolicy |
      | gender                       | noOpPolicy                      |

  Scenario Outline: A record field inherits the drift policy of the dictionary type it references
    Given a record with a field that has dictionary type named "<dictionaryTypeFromBackground>"
    When records are read
    Then the record field is available and has a drift policy of "<expectedDriftPolicyFromBackground>"

    Examples: 
      | dictionaryTypeFromBackground | expectedDriftPolicyFromBackground |
      | singleSlaInDays              | oneZScorePolicy                   |
      | doubleSlaInDays              | twoZScoresPolicy                  |

  Scenario Outline: A record field inherits the empty drift policy of the dictionary type it references
    Given a record with a field that has dictionary type named "<dictionaryTypeFromBackground>"
    When records are read
    Then the record field is available and has a no drift policy specified

    Examples: 
      | dictionaryTypeFromBackground |
      | ssn                          |
      | archivable                   |

  Scenario Outline: A record field overrides the drift policy of the dictionary type it references with a new value
    Given a record with a field that has dictionary type named "<dictionaryTypeFromBackground>" and a drift policy of "<overrideDriftPolicy>"
    When records are read
    Then the record field is available and has a drift policy of "<overrideDriftPolicy>"

    Examples: 
      | dictionaryTypeFromBackground | overrideDriftPolicy        |
      | singleSlaInDays              | oneZScorePlusAveragePolicy |
      | doubleSlaInDays              | noOpPolicy                 |

  Scenario Outline: A record field can refer to a composite instance
    Given a composite named "<compositeType>" with multiple fields
    And a record with a field that has a field with a composite type of "<compositeType>"
    When records are read
    Then the record field is available and has a field with a composite type of "<compositeType>" containing multiple fields

    Examples: 
      | compositeType |
      | Address       |
      | Leaf          |

  Scenario: Data Access is enabled by default for a record
    Given a valid record with data access configuration
    When records are read
    Then the record is available and has data access enabled

  Scenario: Data Access can be disabled for a record
    Given a valid record with data access disabled
    When records are read
    Then the record is available and has data access disabled

  Scenario: Pyspark support is enabled for a Python record
    Given a valid record with pyspark support
    When records are read for a Python project
    Then the record is available and has Pyspark support enabled

  Scenario: Default Python record does not have PySpark support
    Given a valid record with no pyspark support
    When records are read for a Python project
    Then the record is available and has Pyspark support disabled
