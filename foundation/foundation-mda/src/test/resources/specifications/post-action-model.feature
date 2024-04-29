@postAction
Feature: Specify post-actions to be generated

  Scenario Outline: Required post-action model data is not provided
    Given an otherwise valid pipeline with a step containing a post-action with name "<name>" and type "<type>"
    When the pipeline with post-actions is read
    Then the generator throws an exception due to invalid post-action metamodel information

    Examples: 
      | name | type |
      |      | blah |
      | foo  |      |

  Scenario Outline: Required model-conversion post-action model data is not provided
    Given an otherwise valid pipeline with a step containing a model-conversion post-action with model source "<modelSource>" and model target "<modelTarget>"
    When the pipeline with post-actions is read
    Then an error is thrown due to missing post-action metamodel information

    Examples: 
      | modelSource | modelTarget |
      |             | blah        |
      | foo         |             |
