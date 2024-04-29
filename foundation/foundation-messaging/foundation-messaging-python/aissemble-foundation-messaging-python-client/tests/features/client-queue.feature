@queue @IntegrationTest
Feature: Enqueue messages through the python client

  Scenario: Message is queued in the service
    Given a messaging topic named "TopicA" exists in the service
    When a message is queued to "TopicA"
    Then I receive a Future object for my queued message

  Scenario: Message is queued in a topic that doesn't exist
    Given a messaging topic named "TopicA" exists in the service
    And a messaging topic named "TopicD" does not exist in the service
    When a message is queued to "TopicD"
    Then I receive a NoTopicSupportedError instead of a Future