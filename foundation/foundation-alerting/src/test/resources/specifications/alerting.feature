@alerting
Feature: Alerting -> Publish and Subscribe

  As the system, I can subscribe to alerts on a topic.

  Scenario: Publish & subscribe to alerts
    Given I am subscribed to the alert topic
    When an alert is published to the topic
    Then I can see the details of the alert
    