@spark-application-s3-migration
Feature: Migrate a spark application base values with Localstack S3 credentials to use a secret reference
  Scenario: Migrate a spark application base values with Localstack S3 credentials in both driver and executor
    Given a project that has a spark application base values 
    And both the base values executor and driver contain hardcoded Localstack S3 credentials
    And the base values contains "<yaml-config>" configuration
    When the spark application base values S3 migration executes
    Then the base values S3 credentials will be updated to use a secret reference
    And the hardcoded Localstack S3 credentials will be removed

  Examples:
    | yaml-config |
    | env         |
    | envFrom     |

  Scenario: Migrate a spark application base values with Localstack S3 credentials in driver or executor
    Given a project that has a spark application base values 
    And only the base values "<spark-config>" contain hardcoded Localstack S3 credentials
    And the base values contains "<yaml-config>" configuration
    When the spark application base values S3 migration executes
    Then the base values S3 credentials will be updated to use a secret reference
    And the hardcoded Localstack S3 credentials will be removed

  Examples:
    | spark-config | yaml-config |
    | driver       | env         |
    | driver       | envFrom     |
    | executor     | env         |
    | executor     | envFrom     |

  Scenario: Skip spark application base values migration with secret based S3 credentials in the base values
    Given a project that has a spark application base values 
    And the base values contains secret based S3 credentials
    When the spark application base values S3 migration executes
    Then the spark application base values S3 migration is skipped

  Scenario: Skip spark application base values migration without any S3 credentials in the base values
    Given a project that has a spark application base values 
    And the base values does not contain any S3 credentials
    When the spark application base values S3 migration executes
    Then the spark application base values S3 migration is skipped
  
  Scenario: Skip spark application base values migration without any environment variables in the base values
    Given a project that has a spark application base values
    And the base values does not contain any environment variables
    When the spark application base values S3 migration executes
    Then the spark application base values S3 migration is skipped