@docker-pip-migration
Feature: dockerfile migration to ensure failing of Docker build if any Python dependency install fails

  Scenario: Migrate a dockerfile
    Given a project that has a dockerfile with at least one instruction that encompasses multiple installs
     When the docker migration executes
     Then the dockerfile exhibits the correct instructions

  Scenario: Skip dockerfile migration
    Given a project that has a dockerfile with no instructions that encompasses multiple installs
     When the docker migration executes
     Then the docker migration is skipped