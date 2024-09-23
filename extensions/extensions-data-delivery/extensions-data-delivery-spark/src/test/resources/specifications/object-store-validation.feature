@data-delivery @object-store-validation
Feature: Object store credentials can be verified for read and write access
  I want to use configured credentials to connect to an object store and
  verify read and write access

  Scenario: I can retrieve credentials from a Krausening file
    Given a properties file exists
    Then the credentials are used to verify object store connectivity
