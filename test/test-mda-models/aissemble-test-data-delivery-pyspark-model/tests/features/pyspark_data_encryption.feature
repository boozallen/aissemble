@pyspark_data_encryption
Feature: Data encryption
    # Generated sample BDD specification/feature file - PLEASE ***DO*** MODIFY.
    # Originally generated from templates/behave.feature.vm.

  Scenario: Encrypt fields for native inbound with inbound record type (set([CustomData]))
  Given a pipeline with native inbound collection and inbound record type
  When encryption is called on the inbound record
  Then the correct fields are encrypted

  Scenario: Encrypt fields for native non-collection inbound no inbound record type (DataFrame)
  Given a pipeline with native non-collection inbound and no inbound record type
  When encryption is called on the native data set
  Then the correct dataframe fields are encrypted

  Scenario: Encrypt fields for native collection inbound without an inbound record type (set([DataFrame]))
  Given a pipeline with native collection inbound and no inbound record type
  When encryption is called on the native data set
  Then the correct dataframe fields are encrypted for each dataframe

  Scenario: Specifying the AES encryption algorithm yields a custom field encrypted with the corresponding algorithm
  Given a pipeline with native inbound collection and inbound record type
  When AES encryption is requested
  Then the correct AES algorithm is applied to the data set
