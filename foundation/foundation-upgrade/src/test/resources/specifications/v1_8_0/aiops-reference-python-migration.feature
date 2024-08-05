Feature: AIOPS Reference Python migration

  Scenario: Update the foundation core python name
    Given a Python implemented project references aiops_core_filestore.file_store_factory
    When the 1.8.0 aiops reference python migration executes
    Then the reference are updated to aissemble_core_filestore.file_store_factory