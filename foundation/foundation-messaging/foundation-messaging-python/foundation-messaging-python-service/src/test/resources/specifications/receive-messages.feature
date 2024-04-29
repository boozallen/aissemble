@receive-message
Feature: Receive messages through the messaging service

  Scenario: Message is Received from Broker and Processed Successfully
     Given consumer subscribe to a topic named "TopicA"
      When a message is sent to the topic
       And the message is processed successfully by the consumer
      Then an ack is sent to broker

  @nack-message
  Scenario: Message is Received from Broker and Not processed Successfully
     Given consumer subscribe to a topic named "TopicA"
       And a message is sent to the topic
      When consumer failed to process the message
      Then a nack is sent to broker

  @manual-acknowledgement
  Scenario: Manual Acknowledgement of Message Receipt
      Given consumer subscribe to a topic named "TopicA"
        And the subscription is configured with the manual ack strategy
       When a message is received from the topic
       Then the service does not ack or nack the message

  @manual-acknowledgement
  Scenario Outline: Manual Acknowledgement of Message Receipt
    Given consumer subscribe to a topic named "TopicA"
      And the subscription is configured with the manual ack strategy
     When the consumer "<acknowledge>" the message
     Then the "<acknowledge>" is sent to the broker
  Examples:
  | acknowledge |
  | ack         |
  | nack        |
