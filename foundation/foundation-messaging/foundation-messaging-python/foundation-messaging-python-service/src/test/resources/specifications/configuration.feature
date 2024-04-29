@configure
Feature: Configure messaging topics and channels

  Scenario: I can create custom messaging topics
    Given I have configured the messaging library to connect to "TopicA"
    When the messaging service starts
    Then the service creates a new emitter for "TopicA"

  Scenario: I can create multiple custom messaging topics
    Given I have configured the messaging library to connect to 3 topics
    When the messaging service starts
    Then the service creates an emitter for each topic

  @manual
  Scenario: I can configure different brokers for the messaging service
    Given I have configured the messaging library to use a kafka server at localhost:9092
    And I have configured "topicA" to use kafka
    When the messaging service starts
    Then the service will start up kafka to create its channels

  Scenario: I can listen to custom messaging topics
    Given I have configured the messaging library to connect to "TopicA"
     When the messaging service starts
     Then the service creates a new listener for "TopicA"

  Scenario: I can listen multiple custom messaging topics
     Given I have configured the messaging library to connect to 4 topics
      When the messaging service starts
      Then the service creates an listener for each topic

  @manual
  Scenario: All supported brokers are enabled by default
      When the message service is initialized
      Then all supported connectors are loaded
