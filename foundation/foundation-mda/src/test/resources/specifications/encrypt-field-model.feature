@encryptfield
Feature: Encrypting record fields

  Background:
    Given the following dictionary types are defined:
      | name            | simpleType | protectionPolicy | driftPolicy      | ethicsPolicy             |
      | ssn             | string     | hashValuePolicy  |                  |                          |
      | phoneNumber     | string     | hashValuePolicy  |                  |                          |
      | singleSlaInDays | decimal    |                  | oneZScorePolicy  |                          |
      | doubleSlaInDays | decimal    |                  | twoZScoresPolicy |                          |
      | archivable      | boolean    |                  |                  | sampleMinimumPolicy      |
      | gender          | string     |                  |                  | genderDistributionPolicy |

  Scenario Outline: A record field can be designated as a secure field
    Given a record with a field specifying the column name "<columnName>" as secure
    When encrypted records are read
    Then the record field is available and encrypted with the encryption policy "<securityPolicy>"

    Examples: 
      | columnName          | securityPolicy |
      | ssn                 | someEncryptID  |
      | total_amount        | someEncryptID  |

