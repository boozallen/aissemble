@composite
Feature: Specify composite of semantically defined types

  Background: 
    Given the following dictionary types:
      | name          | simpleType | protectionPolicy | driftPolicy                     | ethicsPolicy                        |
      | streetName    | string     | obfuscatePolicy  |                                 |                                     |
      | zipCode       | string     | obfuscatePolicy  |                                 |                                     |
      | airportCode   | string     |                  | profileOutOfStandardRangePolicy |                                     |
      | milesFromHome | string     |                  | fiveMileZonePolicy              |                                     |
      | count         | integer    |                  |                                 | sampleMinimumPolicy                 |
      | stateOfOrigin | string     |                  |                                 | inStateUnivserityDistributionPolicy |

  Scenario Outline: Create a valid composite file
    Given a composite described by "<name>", "<package>"
    When composites are read
    Then a valid composite is available can be looked up by the name "<name>" and "<package>"

    Examples: 
      | name          | package                      |
      | Address       | com.boozallen.aiops.location |
      | NetFlowRecord | com.boozallen.aiops.cyber    |
      | UserProfile   | com.boozallen.aiops.person   |

  Scenario Outline: A composite can have a description
    Given a composite with a description "<description>"
    When composites are read
    Then a valid composite is available with a description of "<description>"

    Examples:
     | description                               |
     | This is a description.                    |
     | This composite represents a User Profile. |

  Scenario Outline: A composite can contain multiple fields
    Given a composite with the fields named:
      | <fieldA> |
      | <fieldB> |
    When composites are read
    Then a valid composite is available with fields named:
      | <fieldA> |
      | <fieldB> |

    Examples: 
      | fieldA      | fieldB      |
      | number      | street      |
      | airportCode | arrivalTime |
      | balls       | strikes     |

  Scenario Outline: A composite field can have a description
    Given a composite with a field that has a description "<description>"
    When composites are read
    Then the composite field is available and has a description of "<description>"

    Examples:
      | description                                        |
      | This is a composite field description.             |
      | This composite field represents the SSN of a user. |

  Scenario Outline: A composite can have a field with any valid dictionary type
    Given a composite with a field that has dictionary type named "<dictionaryTypeName>"
    When composites are read
    Then the composite field is available with a simple type of "<expectedSimpleTypeFromBackground>"

    Examples: 
      | dictionaryTypeName | expectedSimpleTypeFromBackground |
      | streetName         | string                           |
      | airportCode        | string                           |
      | count              | integer                          |

  Scenario Outline: A composite field can specify a column that will be used for persistence
    Given a composite with a field specifying the column name "<columnName>"
    When composites are read
    Then the composite field is available with a column name of "<columnName>"

    Examples: 
      | columnName   |
      | street_name  |
      | airport_code |
      | COUNT        |

  Scenario: A composite field can specify if it is required
    Given a composite with a field specifying the column is required
    When composites are read
    Then the composite field is available and marked as required

  Scenario: A composite field can specify if it is optional
    Given a composite with a field specifying the column as optional
    When composites are read
    Then the composite field is available and marked as optional

  Scenario Outline: A composite field inherits the protection policy of the dictionary type it references
    Given a composite with a field that has dictionary type named "<dictionaryTypeFromBackground>"
    When composites are read
    Then the composite field is available and has a protection policy of "<expectedProtectionPolicyFromBackground>"

    Examples: 
      | dictionaryTypeFromBackground | expectedProtectionPolicyFromBackground |
      | streetName                   | obfuscatePolicy                        |
      | zipCode                      | obfuscatePolicy                        |

  Scenario Outline: A composite field inherits the empty protection policy of the dictionary type it references
    Given a composite with a field that has dictionary type named "<dictionaryTypeFromBackground>"
    When composites are read
    Then the composite field is available and has a no protection policy specified

    Examples: 
      | dictionaryTypeFromBackground |
      | airportCode                  |
      | count                        |

  Scenario Outline: A composite field overrides the protection policy of the dictionary type it references with a new value
    Given a composite with a field that has dictionary type named "<dictionaryTypeFromBackground>" and a protection policy of "<overrideProtectionPolicy>"
    When composites are read
    Then the composite field is available and has a protection policy of "<overrideProtectionPolicy>"

    Examples: 
      | dictionaryTypeFromBackground | overrideProtectionPolicy   |
      | streetName                   | encryptPolicy              |
      | zipCode                      | stripExtendedZipCodePolicy |

  Scenario Outline: A composite field inherits the ethics policy of the dictionary type it references
    Given a composite with a field that has dictionary type named "<dictionaryTypeFromBackground>"
    When composites are read
    Then the composite field is available and has a ethics policy of "<expectedEthicsPolicyFromBackground>"

    Examples: 
      | dictionaryTypeFromBackground | expectedEthicsPolicyFromBackground  |
      | count                        | sampleMinimumPolicy                 |
      | stateOfOrigin                | inStateUnivserityDistributionPolicy |

  Scenario Outline: A composite field inherits the empty ethics policy of the dictionary type it references
    Given a composite with a field that has dictionary type named "<dictionaryTypeFromBackground>"
    When composites are read
    Then the composite field is available and has a no ethics policy specified

    Examples: 
      | dictionaryTypeFromBackground |
      | streetName                   |
      | airportCode                  |

  Scenario Outline: A composite field overrides the ethics policy of the dictionary type it references with a new value
    Given a composite with a field that has dictionary type named "<dictionaryTypeFromBackground>" and a ethics policy of "<overrideEthicsPolicy>"
    When composites are read
    Then the composite field is available and has a ethics policy of "<overrideEthicsPolicy>"

    Examples: 
      | dictionaryTypeFromBackground | overrideEthicsPolicy             |
      | count                        | archiveOlderThanNiceMonthsPolicy |
      | stateOfOrigin                | noOpPolicy                       |

  Scenario Outline: A composite field inherits the drift policy of the dictionary type it references
    Given a composite with a field that has dictionary type named "<dictionaryTypeFromBackground>"
    When composites are read
    Then the composite field is available and has a drift policy of "<expectedDriftPolicyFromBackground>"

    Examples: 
      | dictionaryTypeFromBackground | expectedDriftPolicyFromBackground |
      | airportCode                  | profileOutOfStandardRangePolicy   |
      | milesFromHome                | fiveMileZonePolicy                |

  Scenario Outline: A composite field inherits the empty drift policy of the dictionary type it references
    Given a composite with a field that has dictionary type named "<dictionaryTypeFromBackground>"
    When composites are read
    Then the composite field is available and has a no drift policy specified

    Examples: 
      | dictionaryTypeFromBackground |
      | streetName                   |
      | zipCode                      |

  Scenario Outline: A composite field overrides the drift policy of the dictionary type it references with a new value
    Given a composite with a field that has dictionary type named "<dictionaryTypeFromBackground>" and a drift policy of "<overrideDriftPolicy>"
    When composites are read
    Then the composite field is available and has a drift policy of "<overrideDriftPolicy>"

    Examples: 
      | dictionaryTypeFromBackground | overrideDriftPolicy       |
      | count                        | noOpPolicy                |
      | stateOfOrigin                | tenMileExpandedZonePolicy |
