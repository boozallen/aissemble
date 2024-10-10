Feature: Migrate a Spark pipeline module CDI factoru with the new CDI classes to ensure messaging compatibility with Java 17

  Scenario: A Spark pipeline module with smallrye kafka is migrated to include the messaging CDI context and kafka CDI context
    Given a maven project with the "jar" packaging type
    And a maven project "smallrye-reactive-messaging-kafka" dependency
    When the Spark Pipeline Messaging migration executes
    Then the CDI container factory is updated with the messaging and kafka context objects

  Scenario: A Spark pipeline module with a different smallrye dependency is migrated to include the messaging CDI context
    Given a POM with the "jar" packaging type
    And a maven project "smallrye-reactive-messaging-rabbitmq" dependency
    When the Spark Pipeline Messaging migration executes
    Then the CDI container factory is updated with the messaging context object

  Scenario Outline: A migrated Spark pipeline module will not migrate again
    Given a POM with the "jar" packaging type
    And a maven project "<dependency>" dependency
    And the pipeline module has already been migrated
    When the Spark Pipeline Messaging migration executes
    Then the Spark Pipeline Messaging migration is skipped

    Examples:
      | dependency                           |
      | smallrye-reactive-messaging-kafka    |
      | smallrye-reactive-messaging-rabbitmq |

  Scenario: A Spark pipeline module without a smallrye dependency is not migrated
    Given a POM with the "jar" packaging type
    And no maven project smallrye dependencies
    When the Spark Pipeline Messaging migration executes
    Then the Spark Pipeline Messaging migration is skipped

  Scenario: A Pyspark pipeline module is not migrated
    Given a POM with the "habushu" packaging type
    When the Spark Pipeline Messaging migration executes
    Then the Spark Pipeline Messaging migration is skipped
