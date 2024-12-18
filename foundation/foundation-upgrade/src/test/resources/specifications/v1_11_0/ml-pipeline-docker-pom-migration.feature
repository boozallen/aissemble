Feature: Migrate the Ml pipeline docker POM

   Scenario Outline: Migrate Ml pipeline docker POM to include Ml pipeline step dependency
      Given a "<ml-pipeline-step>" docker POM with no dependencies defined
      When the 1.11.0 ml pipeline docker pom migration executes
      Then the "ml-pipeline-step" dependency is add

      Examples:
        | ml-pipeline-step  |
        | training-step          |
        | inference-step         |

   Scenario Outline: Migrate Ml pipeline docker POM to include Ml pipeline step dependency
      Given a "<ml-pipeline-step>" docker POM with one dependencies defined
      And  the existing dependency artifactId is not "ml-pipeline-step"
      When the 1.11.0 ml pipeline docker pom migration executes
      Then the "ml-pipeline-step" dependency is add

      Examples:
        | ml-pipeline-step  |
        | training-step          |
        | inference-step         |

   Scenario Outline: Skip migration for Ml pipeline docker POM with Ml pipeline step dependency
      Given a "<ml-pipeline-step>" docker POM with the same step dependency defined
      When the 1.11.0 ml pipeline docker pom migration executes
      Then the ml migration is skipped

      Examples:
        | ml-pipeline-step  |
        | training-step          |
        | inference-step         |