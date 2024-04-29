@emission
Feature: Emit data lineage events

  @withoutConsole @cdi
  Scenario: Events can be emitted via Messaging
    Given a run event
    When the event is emitted via messaging
    Then the event is sent over the outbound channel

  @cdi
  Scenario: Events can be emitted via Console
    Given a run event
    When the ConsoleTransport class is added to the CDI container
    And the event is emitted via messaging
    Then the event is received for logging to the console

  @withoutConsole @cdi
  Scenario: Console emission can be disabled
    Given a run event
    When the event is emitted via messaging
    Then the event is not received for logging to the console

  @cdi
  Scenario: Console emission is automatically included based on configuration.
    Given a configuration that opts into console emission
    When the CDI container is created with automatically selected context
    Then the ConsoleTransport class is available

  @cdi
  Scenario: Console emission is automatically excluded based on configuration.
    Given a configuration that opts out of console emission
    When the CDI container is created with automatically selected context
    Then the ConsoleTransport class is not available
