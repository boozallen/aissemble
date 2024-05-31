Feature: AIOPS Reference migration

  Scenario: Update Data Lineage package name
    Given a Java file references the package com.boozallen.aiops.data.lineage
    When the 1.7.0 aiops reference migration executes
    Then the references are updated to com.boozallen.aissemble.data.lineage

  Scenario: Upgrade Foundation Core Java package names
    Given a Java file references aiops in the foundation core java module package imports
    When the 1.7.0 aiops reference migration executes
    Then the references are updated to point to aissmeble rather than aiops in foundation core java packages

  Scenario: Upgrade Foundation Core Java properties files to remove aiops references
    Given a Java file references in the .properties files
    When the 1.7.0 aiops reference migration executes
    Then the references within the .properties files are upgraded