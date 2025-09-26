@habushu-containerize
Feature: Habushu Containerization Migration

  Migrations to prepare projects to leverage Habushu's containerize-dependencies goal to package
  their python pipelines

  Scenario: Habushu modules with dependencies on other Habushu modules use the correct type
    Given a POM with packaging habushu
    And a pom-type dependency on another habushu-packaged module that is in the list of Maven projects
    When the Habushu dependency type migration executes
    Then the type of the monorepo dependency is updated to habushu

  Scenario: ML Training docker pom files are updated to leverage the Habushu containerize goal
    Given an training POM without a Habushu containerize goal
    And the fermenter-mda plugin has the profile aissemble-training-docker
    When the training docker pom migration executes
    Then the POM is updated to use the habushu containerize goal

  Scenario: ML Training docker POM file has Habushu containerize goal, the migration should be skipped
    Given an training POM with a Habushu containerize goal
    And the fermenter-mda plugin has the profile aissemble-training-docker
    When the training docker pom migration executes
    Then the training docker pom migration was skipped

  Scenario: ML Inference docker pom files are updated to leverage the Habushu containerize goal
    Given an inference POM without a Habushu containerize goal
    And the fermenter-mda plugin has the profile aissemble-inference-docker
    When the inference docker pom migration executes
    Then the POM is updated to use the habushu containerize goal

  Scenario: ML Inference docker POM file has Habushu containerize goal, the migration should be skipped
    Given an inference POM with a Habushu containerize goal
    And the fermenter-mda plugin has the profile aissemble-inference-docker
    When the inference docker pom migration executes
    Then the inference docker pom migration was skipped

  Scenario: Non ML Inference docker POM files are not migrated
    Given a non ML Inference docker POM files
    When the inference docker pom migration executes
    Then the inference docker pom migration was skipped

  Scenario: Spark worker docker pom files are updated to leverage the Habushu containerize goal
    Given a spark worker POM without a Habushu containerize goal
    And the fermenter-mda plugin has the profile aissemble-spark-worker-docker
    When the spark worker docker pom migration executes
    Then the spark worker docker POM is updated to use the habushu containerize goal

  Scenario: Spark worker docker POM file has Habushu containerize goal, the migration should be skipped
    Given a spark worker POM with a Habushu containerize goal
    And the fermenter-mda plugin has the profile aissemble-spark-worker-docker
    When the spark worker docker pom migration executes
    Then the spark worker docker pom migration was skipped

  Scenario: Spark worker docker POM file has no Habushu packaged dependency, the migration should be skipped
    Given a spark worker POM without Habushu packaged dependencies
    And the fermenter-mda plugin has the profile aissemble-spark-worker-docker
    When the spark worker docker pom migration executes
    Then the spark worker docker pom migration was skipped