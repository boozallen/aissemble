@filestore @integration
Feature: Filestore

  Scenario: As a user, I'm able to authenticate with a cloud provider
    When I provide valid credentials for the cloud provider
    Then I am successfully logged in

  Scenario: As a user, I'm able to save an object to the cloud provider
    When I save an object
    Then the object is persisted

  Scenario: As a user, I'm able to download an object from a cloud provider
    When I request an object
    Then the object is returned