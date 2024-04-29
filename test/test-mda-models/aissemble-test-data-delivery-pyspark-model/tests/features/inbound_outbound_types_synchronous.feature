@synchronous @inbound @outbound
Feature: Pyspark Pipeline Step -> Synchronous Inbound/Outbound Type Patterns

  Scenario Outline: Void Inbound (with Non-Messaging Outbound)
    When a synchronous step with void inbound type and <outboundType> outbound type is defined
    Then the step execution has no inbound parameter
    Examples:
      | outboundType |
      | void         |
      | native       |

  Scenario Outline: Void Outbound (with Non-Messaging Inbound)
    When a synchronous step with <inboundType> inbound type and void outbound type is defined
    Then the step execution has no outbound return type
    Examples:
      | inboundType |
      | void        |
      | native      |

  Scenario Outline: Native Inbound (with Non-Messaging Outbound)
    When a synchronous step with native inbound type and <outboundType> outbound type is defined
    Then the step execution has an inbound parameter of type "DataFrame"
    Examples:
      | outboundType |
      | void         |
      | native       |

  Scenario Outline: Native Outbound (with Non-Messaging Inbound)
    When a synchronous step with <inboundType> inbound type and native outbound type is defined
    Then the step execution has an outbound return type of "DataFrame"
    Examples:
      | inboundType |
      | void        |
      | native      |

  Scenario Outline: Messaging Inbound
    When a synchronous step with messaging inbound type and <outboundType> outbound type is defined
    Then the step execution base has no inbound parameter because it consumes the inbound from kafka
    And the step execution implementation has an inbound parameter of "str" to handle the inbound from kafka
    Examples:
      # NOTE: messaging inbound with native outbound is not a valid combo
      | outboundType |
      | void         |
      | messaging    |

  Scenario Outline: Messaging Outbound
    When a synchronous step with <inboundType> inbound type and messaging outbound type is defined
    Then the step execution base has no outbound return type because it sends the outbound to kafka
    And the step execution implementation has an outbound return type of "str" to send to kafka
    Examples:
      | inboundType |
      | void        |
      | native      |
      | messaging   |
