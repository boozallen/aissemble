Feature: AIOPS Reference Java package migration

  Scenario: Update the Data Access package name
    Given a Java file references the package com.boozallen.aiops.data.access
    When the 1.8.0 aiops reference java migration executes
    Then the references are updated to com.boozallen.aissemble.data.access

  Scenario: Update the Aissemble Security package name
    Given a Java file references the package com.boozallen.aiops.security
    When the 1.8.0 aiops reference java migration executes
    Then the references are updated to com.boozallen.aissemble.security