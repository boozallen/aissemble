Feature: Migrate a Spark pipeline module pom.xml with the javax servlet-api dependency

  Scenario: Add javax servlet-api dependency spark pipeline pom 
    Given a POM with "jar" packaging type
    And the POM does not contain the javax servlet-api dependency
    When the Spark Pipeline Servlet API migration executes
    Then the javax servlet-api dependency is added to the POM

  Scenario: Skip spark pipeline pom that already has the javax servlet-api dependency
    Given a POM with "jar" packaging type
    And the POM already contains the javax servlet-api dependency
    When the Spark Pipeline Servlet API migration executes
    Then the Spark Pipeline Servlet API migration is skipped

  Scenario: Skip pom that does not have jar packaging
    Given a POM with "habushu" packaging type
    When the Spark Pipeline Servlet API migration executes
    Then the Spark Pipeline Servlet API migration is skipped