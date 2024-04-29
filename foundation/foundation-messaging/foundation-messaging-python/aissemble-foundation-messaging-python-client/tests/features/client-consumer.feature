@consumer
Feature: Consumer subscriptions

  Scenario: I can subscribe to message topics
    Given a messaging topic named "successfulTopic"
    When I subscribe to the topic "successfulTopic"
    Then no exceptions are thrown

  Scenario: I receive an error when subscribing to a topic that doesn't exist
    Given a messaging topic named "notSubscribed"
    When I subscribe to the topic "subscribed"
    Then I receive an exception telling me that it doesn't exist
    And the exception is a TopicNotSupportedError exception
