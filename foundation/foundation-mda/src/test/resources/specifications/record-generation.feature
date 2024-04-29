@record @manual
Feature: Generate records and associated files for semantically defined types

  Scenario: Data access generates sane code for bytearrays
    Given a custom record type with a field of base type `bytearray`
    When data access is enabled for the custom record type
    Then the generated data access code will compile appropriately

  Scenario: Records with bytearray fields are generated as Java objects with settable fields
    Given a custom record type with a field of base type `bytearray`
    When shared Java records are generated
    Then binary data can be set on the generated record

  Scenario: Records with bytearray fields are generated as Python objects with settable fields
    Given a custom record type with a field of base type `bytearray`
    When shared Python records are generated
    Then binary data can be set on the generated record
    And calculated binary fields shall be represented in pyspark Dataframes as a BinaryType