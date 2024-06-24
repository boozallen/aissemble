Feature: Data access is updated to use the v2 thrift server endpoint when upgrading to 1.9.0

  Scenario: A project including data access
    Given a project with data access enabled using the default thrift endpoint
    When the 1.9.0 data access thrift endpoint migration executes
    Then the application.properties file will be updated to reference the new thrift endpoint

  Scenario: A project specifying a non-default thrift endpoint for data access
    Given a project with data access enabled using a non-default thrift endpoint
    When the 1.9.0 data access thrift endpoint migration determines whether to execute
    Then the migration system shall skip migrating the file
