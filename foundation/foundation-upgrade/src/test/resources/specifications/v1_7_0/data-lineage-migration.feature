Feature: Data Lineage upgrade migration

  Scenario: Update Data Lineage package name
    Given a Java file references the package com.boozallen.aiops.data.lineage
    When the 1.7.0 data lineage package migration executes
    Then the references are updated to com.boozallen.aissemble.data.lineage