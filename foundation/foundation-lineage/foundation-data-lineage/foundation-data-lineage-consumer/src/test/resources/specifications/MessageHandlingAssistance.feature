Feature: Asynchronous handling of messages is simplified

  @detectAckType @exceptional
  Scenario Outline: If an exception is encountered in processing, and the failure strategy is <failureStrategy>, then the message response is <ackType>
    Given the failure strategy is configured to <failureStrategy>
    When an exception occurs during message processing
    Then the message acknowledgement is <ackType>
    Examples:
      | failureStrategy | ackType |
      | NACK | NACK |
      | DROP | ACK |

  @detectAckType @successful
  Scenario Outline: If message processing is successful, and the failure strategy is <failureStrategy>, then the message response is <ackType>
    Given the failure strategy is configured to <failureStrategy>
    When message processing occurs successfully
    Then the message acknowledgement is <ackType>
    Examples:
      | failureStrategy | ackType |
      | NACK | ACK |
      | DROP | ACK |