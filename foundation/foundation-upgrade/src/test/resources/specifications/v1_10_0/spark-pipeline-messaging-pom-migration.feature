Feature: Migrate a Spark pipeline module pom.xml with the new CDI classes dependency to ensure messaging compatibility with Java 17

  Scenario: A Spark pipeline module with smallrye kafka is migrated to include the aissemble kafka messaging dependency
    Given a POM with the "jar" packaging type
    And a "smallrye-reactive-messaging-kafka" dependency
    When the Spark Pipeline Messaging Pom migration executes
    Then the aissemble kafka messaging dependency is added to the POM

  Scenario: A Spark pipeline module with a different smallrye dependency is migrated to include the messaging CDI context
    Given a POM with the "jar" packaging type
    And a "smallrye-reactive-messaging-rabbitmq" dependency
    When the Spark Pipeline Messaging Pom migration executes
    Then the Spark Pipeline Messaging Pom migration is skipped

  Scenario Outline: A migrated Spark pipeline module will not migrate again
    Given a POM with the "jar" packaging type
    And a "<dependency>" dependency
    And the pipeline module pom has already been migrated
    When the Spark Pipeline Messaging Pom migration executes
    Then the Spark Pipeline Messaging Pom migration is skipped

    Examples:
      | dependency                           |
      | smallrye-reactive-messaging-kafka    |
      | smallrye-reactive-messaging-rabbitmq |

  Scenario: A Spark pipeline module without a smallrye dependency is not migrated
    Given a POM with the "jar" packaging type
    And no smallrye dependencies
    When the Spark Pipeline Messaging Pom migration executes
    Then the Spark Pipeline Messaging Pom migration is skipped

  Scenario: A Pyspark pipeline module is not migrated
    Given a POM with the "habushu" packaging type
    When the Spark Pipeline Messaging Pom migration executes
    Then the Spark Pipeline Messaging Pom migration is skipped
