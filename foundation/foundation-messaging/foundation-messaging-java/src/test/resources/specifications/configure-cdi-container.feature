@cdiConfiguration
Feature: Messaging -> CDI Configuration
  
  As a developer, I need to be able to configure instances of a CDI container

  Scenario Outline: Add a class to the CDI container
    Given I registered a "<class>" in the CDI configuration
    When the container is initialized
    Then the "<class>" can be found in the CDI container

    Examples: 
      | class                                               |
      | com.boozallen.aissemble.messaging.example.ACdiComponent       |
      | com.boozallen.aissemble.messaging.example.AnotherCdiComponent |

  Scenario: Add a list of classes to the CDI container
    Given I registered a list of classes in the CDI configuration
      | com.boozallen.aissemble.messaging.example.ACdiComponent       |
      | com.boozallen.aissemble.messaging.example.AnotherCdiComponent |
    When the container is initialized
    Then the classes can be found in the CDI container
