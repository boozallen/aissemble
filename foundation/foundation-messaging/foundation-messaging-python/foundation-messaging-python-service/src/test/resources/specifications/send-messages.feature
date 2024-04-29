@send
Feature: Send messages through the messaging service

  Scenario: Message is queued in the service
    Given a messaging topic named "TopicA"
    When a message is queued to "TopicA"
    Then a message is sent to to the topic "TopicA"

  Scenario: Topic cannot be found
    Given a messaging topic named "TopicA"
    When a message is queued to "TopicD"
    Then an exception is thrown saying the topic was not found