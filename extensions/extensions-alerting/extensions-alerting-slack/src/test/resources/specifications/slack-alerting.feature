@alerting
Feature: Alerting -> Slack notifications

As the system, I can send alerts to a configured slack channel
  
  Scenario: Send alerts to a configured slack channel
    When an alert is sent to slack
    Then the alert is sent to the configured slack channel successfully
    