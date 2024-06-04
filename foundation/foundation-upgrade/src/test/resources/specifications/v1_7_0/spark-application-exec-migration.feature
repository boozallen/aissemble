@habushu-build-cache-migration
Feature: Spark App Exec Migration

  Scenario: Update a standard project to the new Chart URL
    Given a pipeline pom file with one or more helm template commands using the aissemble-spark-application chart
    When the 1.7.0 spark app exec migration executes
    Then the pom is updated to use the aissemble-spark-application-chart from the fully qualified URL