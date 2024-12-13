@extend-metamodel
Feature: Extend foundation-mda so that you can modify existing metamodel schemas

  Scenario Outline: Foundation-mda metamodel elements are extendable
    Given a class extending the "<type>" metamodel with new simple and complex fields
    When the "<profile>" profile is generated
    Then the generation completes successfully
    And the new "<type>" metamodel fields can be accessed

    Examples:
      | type       | profile                 |
      | dictionary | java-dictionary-type    |
      | record     | java-record             |
      | pipeline   | aissemble-maven-modules |
      | composite  |                         | # Composite is not currently used in any generators
