@teams @manual
Feature: Alerting -> Teams notifications

  As the system, I can send alerts to a configured Microsoft Teams channel

  Scenario: Send alerts to a configured teams channel
    When an alert is sent to teams
    Then the alert is sent to the configured teams channel successfully
