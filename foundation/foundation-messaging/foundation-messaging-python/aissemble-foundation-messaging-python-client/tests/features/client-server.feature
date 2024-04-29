@IntegrationTest
Feature: client-server integration

  Scenario: I can subscribe to an existing messaging topic
    Given a messaging topic "TopicA" that exists in the service
    When the messaging client subscribes to "TopicA"
    Then the messaging client confirms the subscription to "TopicA"

  Scenario: I cannot subscribe to a non-existent topic
    Given a messaging topic "TopicA" that exists in the service
    And a messaging topic "TopicD" that does not exist in the service
    When the messaging client subscribes to "TopicD"
    Then I receive a NoTopicSupportedError

  Scenario Outline: Message is Received from Broker and Processed Successfully
     Given consumer subscribe to a topic named "TopicA"
       And the subscription is configured with the <ackStrategy> strategy
      When a message is sent to the topic
      Then the message is processed successfully by the consumer
  Examples:
  | ackStrategy           |
  | MANUAL                |
  | POSTPROCESSING        |


  @manual-acknowledgement
  Scenario Outline: Manual Acknowledgement of Message Receipt
    Given consumer subscribe to a topic named "TopicA"
      And the subscription is configured with the MANUAL strategy
     When the consumer <acknowledge> the message
     Then the <acknowledge> is sent to the broker
  Examples:
  | acknowledge |
  | ack         |
  | nack        |