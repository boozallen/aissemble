Feature: Migrate the docker module POM dependency type

  Scenario Outline: Migrate docker module pom dependency type when the type is pom
    Given a docker module pom that has the "<artifact-id>" dependency with pom type
    When the docker module pom dependency type migration executes
    Then the docker module pom dependency type is changed to "<type>"

    Examples:
      | artifact-id      | type     |
      | spark-pipeline   |          |
      | pyspark-pipeline | habushu  |

  Scenario Outline: Skip migration when the docker pom has expected dependency type
    Given a docker module pom that has the "<artifact-id>" dependency with "<type>" type
    When the docker module pom dependency type migration executes
    Then the docker module pom dependency type migration is skipped

    Examples:
      | artifact-id      | type     |
      | spark-pipeline   |          |
      | pyspark-pipeline | habushu  |