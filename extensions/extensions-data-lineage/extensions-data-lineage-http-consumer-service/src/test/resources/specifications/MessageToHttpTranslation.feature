@http
Feature: Incoming messages are processed for submission to an HTTP endpoint

  @endpointExists
  Scenario: Incoming message content is submitted to the HTTP endpoint
    Given the HTTP endpoint is available
    When a message is sent to the incoming channel
    Then the process completes successfully
    And the message payload is sent to the HTTP endpoint

  @detectAckType @endpointNotExists
  Scenario Outline: If an exception is encountered in processing, and the failure strategy is <failureStrategy>, then the message response is <ackType>
    Given the HTTP endpoint is unavailable
    And the failure strategy is configured to <failureStrategy>
    When a message attempts processing
    Then the message acknowledgement is <ackType>
    Examples:
      | failureStrategy | ackType |
      | NACK | NACK |
      | DROP | ACK |

  @detectAckType @endpointExists
  Scenario Outline: If an exception is not encountered in processing, and the failure strategy is <failureStrategy>, then the message response is <ackType>
    Given the HTTP endpoint is available
    And the failure strategy is configured to <failureStrategy>
    When a message attempts processing
    And the message acknowledgement is <ackType>
    Examples:
      | failureStrategy | ackType |
      | NACK | ACK |
      | DROP | ACK |