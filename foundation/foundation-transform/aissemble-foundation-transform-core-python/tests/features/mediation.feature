Feature: Configure and Execute Mediation

    Background:
        Given the following mediation configurations:
            | inputType         | outputType         | className                                         |
            | json              | json               | data_transform_core.mediator.LoggingMediator      |
            | bronze-v2         | bronze-v3          | data_transform_core.mediator.LoggingMediator      |
            | silver-v1         | gold-v2            | data_transform_core.mediator.DoesNotExistMediator |
            | mixed-case-string | lower-case-string  | test_mediators.LowercaseMediator                  |
            | anything          | never-gonna-happen | test_mediators.ExceptionalMediator                |

    Scenario Outline: Load multiple mediation routines
        When mediation is configured for runtime
        Then a valid mediation routine is available for the intersection of "<inputType>" and "<outputType>"

        Examples:
            | inputType | outputType |
            | json      | json       |
            | bronze-v2 | bronze-v3  |

    Scenario: Non-existent class defined
        When mediation is configured for runtime
        Then a valid mediation routine is NOT available for the intersection of "silver-v1" and "gold-v2"

    Scenario: Non-existent mediation routine requested
        When mediation is configured for runtime
        Then a valid mediation routine is NOT available for the intersection of "foo" and "????"

    Scenario Outline: Validate mediation execution
        When mediation is configured for runtime
        And the mediator is invoked for input "<inputType>" and output "<outputType>" values "<inputValue>"
        Then the input is transformed to "<outputValue>"

        Examples:
            | inputType         | outputType        | inputValue        | outputValue       |
            | json              | json              | log this          | log this          |
            | bronze-v2         | bronze-v3         | DO NOT CHANGE ME  | DO NOT CHANGE ME  |
            | mixed-case-string | lower-case-string | I LoVe mIxEd CAse | i love mixed case |
            | no match          | reflective        | pass this through | pass this through |

    Scenario: Exception Handling
        When mediation is configured for runtime
        And the mediator is invoked for input "anything" and output "never-gonna-happen" values "input theater"
        Then a graceful exception case is returned

    Scenario: Configure mediation routine with properties
        Given a mediator with "string", "string", and "test_mediators.PropertyAwareMediator"
        And the following properties:
            | key       | value |
            | propertyA | foo   |
            | propertyB | bar   |
        When mediation is configured for runtime
        And the mediator is invoked for input "string" and output "string" values "SOMETHING"
        Then the input is transformed to "foo-SOMETHING-bar"
