Feature: IT infrastructure is updated to use JDK 17

  Scenario: Update a project that is using JDK 11 docker image
    Given a project with outdated java docker image
    When the it infrastructure migration executes
    Then the docker image being used is JDK 17

  Scenario: A project already using JDK 17 image is not updated
    Given a project that is using the java 17 docker image
    When the it infrastructure migration executes
    Then the migration was skipped
