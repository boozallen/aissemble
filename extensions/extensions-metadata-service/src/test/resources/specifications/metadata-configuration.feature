@metadata
Feature: The correct status is returned depending on the state of the system when metadata is retrieved
  I want to have the system return the correct status code depending on the state of the system

  Scenario: The metadata endpoint is hit when the table is populated and a 200 status is returned
    Given a populated metadata table
    When the getMetadata endpoint is called
    Then return a 200 status code