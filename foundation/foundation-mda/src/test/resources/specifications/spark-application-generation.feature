@spark-application-generation
Feature: Generating spark application pipeline resources

  @pipeline-generation @code-generation
  Scenario: Pipeline generates file store environment variables necessary for accessing a single configured file store
    Given a spark project named "example"
    And a data-flow pipeline using data-delivery-spark
    And a file store named "s3TestModelOne"
    When the profile data-delivery-spark-pipeline is generated
    Then the "s3TestModelOne_FS_PROVIDER", "s3TestModelOne_FS_ACCESS_KEY_ID", and "s3TestModelOne_FS_SECRET_ACCESS_KEY" configurations "are" generated

  @pipeline-generation @code-generation
  Scenario: Pipeline does not generate file store environment variables for accessing a file store when it does not have a file store configured
    Given a spark project named "example"
    And a data-flow pipeline using data-delivery-spark
    When the profile data-delivery-spark-pipeline is generated
    Then the "s3TestModelOne_FS_PROVIDER", "s3TestModelOne_FS_ACCESS_KEY_ID", and "s3TestModelOne_FS_SECRET_ACCESS_KEY" configurations "aren't" generated

  @pipeline-generation @code-generation
  Scenario: Pipeline generates file store environment variables necessary for accessing multiple configured file stores
    Given a spark project named "example"
    And a data-flow pipeline using data-delivery-spark
    And two file stores named "s3TestModelOne" and "s3TestModelTwo"
    When the profile data-delivery-spark-pipeline is generated
    Then the "s3TestModelOne_FS_PROVIDER", "s3TestModelOne_FS_ACCESS_KEY_ID", and "s3TestModelOne_FS_SECRET_ACCESS_KEY" configurations "are" generated
    And the "s3TestModelTwo_FS_PROVIDER", "s3TestModelTwo_FS_ACCESS_KEY_ID", and "s3TestModelTwo_FS_SECRET_ACCESS_KEY" configurations "are" generated

