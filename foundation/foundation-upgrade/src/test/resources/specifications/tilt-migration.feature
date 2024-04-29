Feature: Tilt file upgrade migration

  Scenario: Migrate the Tiltfile VERSION_AISSEMBLE when the VERSION_AISSEMBLE is less than the aissemble project version.
    Given an existing Tiltfile with VERSION_AISSEMBLE is less than the project aiSSEMBLE version
    When the Tiltfile version migration executes
    Then the VERSION_AISSEMBLE is updated to the project version

  Scenario: Do not migrate the Tiltfile VERSION_AISSEMBLE when the VERSION_AISSEMBLE version is greater than the project version.
    Given an existing Tiltfile with VERSION_AISSEMBLE is greater than the project version
    When the Tiltfile version migration executes
    Then the VERSION_AISSEMBLE should not be migrated

  Scenario: Do not migrate the Tiltfile VERSION_AISSEMBLE when the VERSION_AISSEMBLE version is equal to the project version.
    Given an existing Tiltfile with VERSION_AISSEMBLE is equal to the project version
    When the Tiltfile version migration executes
    Then the VERSION_AISSEMBLE should not be migrated

  Scenario: Migrate the Tiltfile VERSION_AISSEMBLE when the VERSION_AISSEMBLE is an older SNAPSHOT version to the project aiSSEMBLE version.
    Given an existing Tiltfile with VERSION_AISSEMBLE is an older SNAPSHOT version than the project aiSSEMBLE version
    When the Tiltfile version migration executes
    Then the VERSION_AISSEMBLE is updated to the specified version value from the Baton specifications in the Tiltfile

  Scenario: Do not migrate the Tiltfile VERSION_AISSEMBLE when the VERSION_AISSEMBLE is a greater version SNAPSHOT version.
    Given an existing Tiltfile with VERSION_AISSEMBLE SNAPSHOT is greater than the project version
    When the Tiltfile version migration executes
    Then the VERSION_AISSEMBLE should not be migrated

  Scenario: Migrate the Tiltfile VERSION_AISSEMBLE when the VERSION_AISSEMBLE is less than the project aissemble version and the version in Tiltfile is x.x.xx format.
    Given an existing Tiltfile with VERSION_AISSEMBLE is less than the project aiSSEMBLE version in x.xx.xx format
    When the Tiltfile version migration executes
    Then the VERSION_AISSEMBLE is updated to the project version

  Scenario: Do not migrate the Tiltfile VERSION_AISSEMBLE when the VERSION_AISSEMBLE is greater than the project aissemble version and the version in Tiltfile is x.x.xx format.
    Given an existing Tiltfile with VERSION_AISSEMBLE is greater than the project aiSSEMBLE version in x.xx.xx format
    When the Tiltfile version migration executes
    Then the VERSION_AISSEMBLE should not be migrated

