@pyspark_data_encryption @integration
Feature: Data encryption

  Scenario: Specifying the Vault encryption algorithm yields a custom field encrypted with the corresponding algorithm
  Given a pipeline with native inbound collection and inbound record type
  When Vault encryption is requested
  Then the correct Vault algorithm is applied to the data set

  Scenario: Specifying the Vault encryption algorithm yields a dataframe field encrypted with the corresponding algorithm
  Given a pipeline with native non-collection inbound and no inbound record type
  When Vault encryption is requested
  Then the correct Vault algorithm is applied to the dataframe

  Scenario: Encrypt fields for native collection inbound without an inbound record type (set([DataFrame]))
  Given a pipeline with native collection inbound and no inbound record type
  When Vault encryption is requested
  Then the correct dataframe fields are vault encrypted for each dataframe

