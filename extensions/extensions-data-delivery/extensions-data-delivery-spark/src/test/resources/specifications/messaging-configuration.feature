@data-delivery @messaging
Feature: Pipeline messaging steps can be configured
  I want to configure how messaging steps connect to external systems without being tied to the internal details of the
  generated step classes.

  Scenario: I can configure a step with outgoing and incoming messaging
    Given a step with outgoing and incoming messaging
    And a pipeline configuration for the step
    When messages flow from the external system
    Then the configuration directs the messaging to the step
    And the configuration directs the step result to the external system

  Scenario: I can configure a step with only incoming messaging
    Given a step with incoming messaging
    And a pipeline configuration for the step
    When messages flow from the external system
    Then the configuration directs the messaging to the step

  Scenario: I can configure a step with only outgoing messaging
    Given a step with outgoing messaging
    And a pipeline configuration for the step
    When the step is executed
    Then the configuration directs the step result to the external system

  @manual
  Scenario: I can change the channel names and use the same configuration
    Given a step with outgoing messaging
    And a pipeline configuration for the step
    When the outgoing channel name is changed
    And the step is executed
    Then the configuration directs the step result to the external system

  Scenario: I can override the base configuration for specific environments
    Given a step with override configurations
    And a pipeline configuration for the step
    When messages flow from the external system
    Then the configuration directs the messaging to the step
    And the configuration directs the step result to the external system
