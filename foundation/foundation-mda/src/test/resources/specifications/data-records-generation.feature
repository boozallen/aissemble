@data-records-generation
Feature: Generating pipeline resources

  @module-generation
  Scenario Outline: Default data module - one language support
    Given a project named "example"
    And a "<type>" pipeline using "<implementation>"
    And a dictionary and 0 or more record models
    When the profile "aissemble-data-records-combined-module" is generated
    Then a module with Spark functionality is generated with "<packaging>" under "example-data-records"
    And the "example-data-records" module generates the profile "<dataRecordProfile>"
    And the user is notified that the module "example-data-records" must be added to the parent POM

    Examples:
      | type      | implementation        | packaging | dataRecordProfile                          |
      | data-flow | data-delivery-spark   | jar       | data-delivery-combined-data-records        |
      | data-flow | data-delivery-pyspark | habushu   | data-delivery-combined-data-records-python |

  @module-generation
  Scenario: Default data module - two language support
    Given a project named "example"
    And a "data-flow" pipeline using "data-delivery-spark"
    And a "data-flow" pipeline using "data-delivery-pyspark"
    And a dictionary and 0 or more record models
    When the profile "aissemble-data-records-combined-module" is generated
    Then a module with Spark functionality is generated with "jar" under "example-data-records-java"
    And a module with Spark functionality is generated with "habushu" under "example-data-records-python"
    And the user is notified that the module "example-data-records-java" must be added to the parent POM
    And the "example-data-records-java" module generates the profile "data-delivery-combined-data-records"
    And the user is notified that the module "example-data-records-python" must be added to the parent POM
    And the "example-data-records-python" module generates the profile "data-delivery-combined-data-records-python"

  @module-generation
  Scenario Outline: Split data module - one language support
    Given a project named "example"
    And a "<type>" pipeline using "<implementation>"
    And a dictionary and 0 or more record models
    When the profile "aissemble-data-records-separate-module" is generated
    Then a semantic-data module is generated with "<packaging>" under "example-data-records-core"
    And the "example-data-records-core" module generates the profile "<dataRecordProfile>"
    And the user is notified that the module "example-data-records-core" must be added to the parent POM
    And a module with Spark functionality is generated with "<packaging>" under "example-data-records-spark"
    And the "example-data-records-spark" module generates the profile "<sparkProfile>"
    And the user is notified that the module "example-data-records-spark" must be added to the parent POM
    And "example-data-records-spark" has a dependency on "example-data-records-core"

    Examples:
      | type      | implementation        | packaging | dataRecordProfile                      | sparkProfile               |
      | data-flow | data-delivery-spark   | jar       | data-delivery-data-records-core-java   | data-delivery-data-spark   |
      | data-flow | data-delivery-pyspark | habushu   | data-delivery-data-records-core-python | data-delivery-data-pyspark |

  @module-generation
  Scenario: Split data module - two language support
    Given a project named "example"
    And a "data-flow" pipeline using "data-delivery-spark"
    And a "data-flow" pipeline using "data-delivery-pyspark"
    And a dictionary and 0 or more record models
    When the profile "aissemble-data-records-separate-module" is generated
    Then a semantic-data module is generated with "jar" under "example-data-records-core-java"
    And a semantic-data module is generated with "habushu" under "example-data-records-core-python"
    And a module with Spark functionality is generated with "jar" under "example-data-records-spark-java"
    And a module with Spark functionality is generated with "habushu" under "example-data-records-spark-python"
    And "example-data-records-spark-java" has a dependency on "example-data-records-core-java"
    And "example-data-records-spark-python" has a dependency on "example-data-records-core-python"
    And the user is notified that the module "example-data-records-core-java" must be added to the parent POM
    And the user is notified that the module "example-data-records-core-python" must be added to the parent POM
    And the user is notified that the module "example-data-records-spark-java" must be added to the parent POM
    And the user is notified that the module "example-data-records-spark-python" must be added to the parent POM

  @module-generation
  Scenario Outline: Language becomes mixed over time
    Given a project named "example"
    And a "data-flow" pipeline using "data-delivery-spark"
    And a "data-flow" pipeline using "data-delivery-pyspark"
    And a dictionary and 0 or more record models
    And a pre-existing semantic data module called "example-data-records" with "<existingPackaging>"
    When the profile "aissemble-data-records-combined-module" is generated
    Then a module with Spark functionality is generated with "<newPackaging>" under "example-data-records-<newLanguage>"
    And the user is notified that the module "example-data-records-<newLanguage>" must be added to the parent POM
    And no module is generated under "example-data-records-<existingLanguage>"

    Examples:
      | existingPackaging | existingLanguage | newPackaging | newLanguage |
      | jar               | java             | habushu      | python      |
      | habushu           | python           | jar          | java        |

  @module-generation
  Scenario Outline: Language becomes mixed over time - split module
    Given a project named "example"
    And a "data-flow" pipeline using "data-delivery-spark"
    And a "data-flow" pipeline using "data-delivery-pyspark"
    And a dictionary and 0 or more record models
    And a pre-existing semantic data module called "example-data-records-core" with "<existingPackaging>"
    And a pre-existing semantic data module called "example-data-records-spark" with "<existingPackaging>"
    When the profile "aissemble-data-records-separate-module" is generated
    Then a semantic-data module is generated with "<newPackaging>" under "example-data-records-core-<newLanguage>"
    And a module with Spark functionality is generated with "<newPackaging>" under "example-data-records-spark-<newLanguage>"
    And "example-data-records-spark-<newLanguage>" has a dependency on "example-data-records-core-<newLanguage>"
    And no module is generated under "example-data-records-core-<existingLanguage>"
    And no module is generated under "example-data-records-spark-<existingLanguage>"
    And the user is notified that the module "example-data-records-core-<newLanguage>" must be added to the parent POM
    And the user is notified that the module "example-data-records-spark-<newLanguage>" must be added to the parent POM

    Examples:
      | existingPackaging | existingLanguage | newPackaging | newLanguage |
      | jar               | java             | habushu      | python      |
      | habushu           | python           | jar          | java        |

  @module-generation
  Scenario Outline: No semantic data models
    Given a project named "example"
    And a "data-flow" pipeline using "<implementation>"
    When the profile "<profile>" is generated
    Then no module is generated under "example-data-records"
    Then no module is generated under "example-data-records-core"
    Then no module is generated under "example-data-records-spark"

    Examples:
      | implementation        | profile                                |
      | data-delivery-spark   | aissemble-data-records-combined-module |
      | data-delivery-pyspark | aissemble-data-records-combined-module |
      | data-delivery-spark   | aissemble-data-records-separate-module |
      | data-delivery-pyspark | aissemble-data-records-separate-module |

  @code-generation
  Scenario Outline: Combined profile generation
    Given a project named "example"
    And a "<type>" pipeline using "<implementation>"
    And a dictionary and 0 or more record models
    When the profile "<dataRecordProfile>" is generated
    Then the core semantic-data classes are generated in the module
    And the Spark functionality is generated in the module

    Examples:
      | type      | implementation        | dataRecordProfile                          |
      | data-flow | data-delivery-spark   | data-delivery-combined-data-records        |
      | data-flow | data-delivery-pyspark | data-delivery-combined-data-records-python |

  @code-generation
  Scenario Outline: Core profile generation
    Given a project named "example"
    And a "<type>" pipeline using "<implementation>"
    And a dictionary and 0 or more record models
    When the profile "<dataRecordProfile>" is generated
    Then the core semantic-data classes are generated in the module

    Examples:
      | type      | implementation        | dataRecordProfile                      |
      | data-flow | data-delivery-spark   | data-delivery-data-records-core-java   |
      | data-flow | data-delivery-pyspark | data-delivery-data-records-core-python |

  @code-generation
  Scenario: Spark profile generation
    Given a project named "example"
    And a "data-flow" pipeline using "data-delivery-spark"
    And a dictionary and 0 or more record models
    When the profile "data-delivery-data-spark" is generated
    Then the Spark functionality is generated in the module

  @code-generation
  Scenario: PySpark profile generation
    Given a project named "example"
    And a "data-flow" pipeline using "data-delivery-pyspark"
    And a dictionary and 0 or more record models
    When the profile "data-delivery-data-pyspark" is generated
    Then the Spark functionality is generated in the module
    And the pyproject.toml file has a dependency on "example-data-records-core"

####   Pipeline module behavior:
  @pipeline-generation @module-generation
  Scenario Outline: Pipelines depend on data module by default
    Given a project named "example"
    And a "<type>" pipeline using "<implementation>"
    And a dictionary and 0 or more record models
    When the profile "aissemble-maven-modules" is generated
    Then the pipeline POM has a dependency on "example-data-records"

    Examples:
      | type      | implementation        |
      | data-flow | data-delivery-spark   |
      | data-flow | data-delivery-pyspark |

  @pipeline-generation @module-generation
  Scenario: Pipelines depend on data module by default - two language support
    Given a project named "example"
    And a "data-flow" pipeline using "data-delivery-spark" named "SparkPipeline"
    And a "data-flow" pipeline using "data-delivery-pyspark" named "PySparkPipeline"
    And a dictionary and 0 or more record models
    When the profile "aissemble-maven-modules" is generated
    Then the "SparkPipeline" pipeline POM has a dependency on "example-data-records-java"
    Then the "PySparkPipeline" pipeline POM has a dependency on "example-data-records-python"

  @pipeline-generation @code-generation
  Scenario: Pipeline pyproject includes data module by default
    Given a project named "example"
    And a "data-flow" pipeline using "data-delivery-pyspark"
    And a dictionary and 0 or more record models
    When the profile "data-delivery-pyspark-pipeline" is generated
    Then the pyproject.toml file has a dependency on "example-data-records"

  @pipeline-generation @module-generation
  Scenario: Pipeline pyproject includes data module by default - two language support
    Given a project named "example"
    And a "data-flow" pipeline using "data-delivery-spark"
    And a "data-flow" pipeline using "data-delivery-pyspark"
    And a dictionary and 0 or more record models
    When the profile "data-delivery-pyspark-pipeline" is generated
    Then the pyproject.toml file has a dependency on "example-data-records-python"

  @pipeline-generation @module-generation
  Scenario Outline: Language becomes mixed over time - pipeline POM
    Given a project named "example"
    And a "data-flow" pipeline using "data-delivery-spark" named "SparkPipeline"
    And a "data-flow" pipeline using "data-delivery-pyspark" named "PySparkPipeline"
    And a dictionary and 0 or more record models
    And a pre-existing semantic data module called "example-data-records" with "<packaging>"
    When the profile "aissemble-maven-modules" is generated
    Then the "<defaultPipeline>" pipeline POM has a dependency on "example-data-records"
    Then the "<specificPipeline>" pipeline POM has a dependency on "<specificDataRecords>"

    Examples:
      | packaging | defaultPipeline | specificPipeline | specificDataRecords         |
      | jar       | SparkPipeline   | PySparkPipeline  | example-data-records-python |
      | habushu   | PySparkPipeline | SparkPipeline    | example-data-records-java   |

  @pipeline-generation @module-generation
  Scenario Outline: Language becomes mixed over time - pipeline pyproject
    Given a project named "example"
    And a "data-flow" pipeline using "data-delivery-spark"
    And a "data-flow" pipeline using "data-delivery-pyspark"
    And a dictionary and 0 or more record models
    And a pre-existing semantic data module called "example-data-records" with "<packaging>"
    When the profile "data-delivery-pyspark-pipeline" is generated
    Then the pyproject.toml file has a dependency on "<dataRecordModule>"

    Examples:
      | packaging | dataRecordModule            |
      | habushu   | example-data-records        |
      | jar       | example-data-records-python |

  @pipeline-generation
  Scenario Outline: Semantic data support generated within pipeline
    Given a project named "example"
    And a "<type>" pipeline using "<implementation>"
    And a dictionary and 0 or more record models
    When the profile "<fermenterProfile>" is generated
    Then the core semantic-data classes are generated in the module
    And the Spark functionality is generated in the module

    Examples:
      | type      | implementation        | fermenterProfile      |
      | data-flow | data-delivery-spark   | data-delivery-spark   |
      | data-flow | data-delivery-pyspark | data-delivery-pyspark |