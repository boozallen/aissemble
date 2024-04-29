@subscribe
Feature: Subscribe to a channel listener through the messaging service

  Scenario: The messaging service subscribes to a topic
    Given a messaging topic "TopicA"
    And a valid callback and ack strategy
    When the service creates a subscription to "TopicA"
    Then the service is subscribed to "TopicA"

  Scenario: The messaging service cannot subscribe to a non-existent topic
    Given a messaging topic "TopicA"
    And a valid callback and ack strategy
    When the service creates a subscription to "TopicD"
    Then an exception is thrown saying the topic does not exist