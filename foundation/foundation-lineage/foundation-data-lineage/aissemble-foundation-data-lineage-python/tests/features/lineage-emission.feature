@emission @manual
Feature: Data lineage information can be emitted for consumption by services or users.

  @kafka
  Scenario: A valid request is issued to emit data lineage information to kafka.
    Given a kafka consumer monitoring the lineage topic
    When a valid kafka_emit request is issued
    Then a message will be present in the kafka topic

  @kafka
  Scenario: Events can be emitted through the OpenLineage console transport
    Given a run event
    When the event is emitted via console
    Then the appropriate logrecord will be created

  Scenario: I want to emit message using a custom messaging configuration
      Given a custom messaging configuration
       When set emitter with the custom messaging configuration
       Then the configuration properties have been updated