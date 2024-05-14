@habushu-build-cache-migration
Feature: Adds the <build><directory>dist</directory></build> tag to the pom file if not present.

  Scenario: A pom file specifying Habushu packaging is successfully migrated to specify the build directory
    Given a habushu-packaged pom file where the build directory is unspecified
    When the habushu build cache migration executes
    Then the pom file contains a specification of the build directory

  Scenario: A pom file specifying Habushu packaging with no build block is successfully migrated to specify the build directory
    Given a habushu-packaged pom file where the build block is unspecified
    When the habushu build cache migration executes
    Then the pom file contains a specification of the build directory

  Scenario: A pom file that does not specify Habushu packaging will not be migrated.
    Given a non-habushu-packaged pom file where the packaging is unspecified
    When the habushu build cache migration executes
    Then the pom file will not be marked for migration

  Scenario: A pom file specifying Habushu packaging already specifies the build directory
    Given a pom file where the build directory is already specified
    When the habushu build cache migration executes
    Then the pom file will not be marked for migration