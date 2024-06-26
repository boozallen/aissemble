Feature: AIOPS Reference PDP Python migration

  Scenario: Rename aiops-security.properties file for the pdp docker python module
    Given a Python implemented project policy decision point docker properties file is referencing aiops
    When the 1.8.0 aiops reference pdp python migration executes
    Then the properties file will be renamed to aissemble-security.properties

  Scenario: Update the target path for renamed properties file in the Dockerfile
    Given a Python implemented project policy decision point docker Dockerfile is referencing aiops in the target path of the properties file
    When the 1.8.0 aiops reference pdp python migration executes
    Then the target path in the Dockerfile will be refactored to referenced aissemble-security.properties