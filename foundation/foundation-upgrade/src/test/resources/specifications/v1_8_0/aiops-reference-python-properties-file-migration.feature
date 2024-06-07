Feature: Migrating aiops-security.properties and related files

#  TODO: include the following scenarios inside next part of the feature to rename AIOPS to aiSSEMBLE for the foundation-core-python modules

#  Scenario: Update the values inside of the aissemble-security.properties file
#    Given a Python implemented projects policy decision point docker properties file is referencing aiops
#    When the 1.8.0 aiops reference python properties file migration executes
#    Then the values within the properties file will be updated to reference aissemble instead of aiops

#  Scenario: Update the target path inside of the Dockerfile to reference the renamed aissemble-security.properties file in policy decision point docker
#    Given a Python implemented projects policy decision point docker Dockerfile is referencing an aiops in the properties file target path
#    When the 1.8.0 aiops reference python properties file migration executes
#    Then the target path will be updated to reference the renamed aissemble-security.properties path name

#  Scenario: Update the aiops-security.properties name and related files
#    Given a Python implemented project generates a new properties file after the 1.8.0 upgrade and the aiops-security.properties still exists
#    When the 1.8.0 aiops reference python properties file migration executes
#    Then the existing aiops-security.properties will be renamed to aissemble-security.properties