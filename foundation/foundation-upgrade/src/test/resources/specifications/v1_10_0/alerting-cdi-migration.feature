Feature: Migrate CDI Container Factory to include Alerting Context
  In the cleanup work done for the Quarkus upgrade, the CDI contexts were cleaned up to remove duplicated classes added
  to the pipeline context in lieu of layering contexts from used modules.  Because the pipeline context is regenerated
  on each build, adding alerting after initial generation worked seamlessly. With 1.10, this will no longer be the case.
  To reduce confusion for projects that were relying on this incidental affect, this migration adds the Alerting context
  to the CDI container factory as long as the project depends on `foundation-alerting`.

  Scenario: The default CDI container factory is migrated
    Given a project that depends on `foundation-alerting`
    And the default CDI container factory class that does not add the alerting context
    When the v1.10 alerting CDI migration executes
    Then the file is migrated successfully

  Scenario: The a CDI container that already imports AlertingCdiContext is skipped
    Given a project that depends on `foundation-alerting`
    And the default CDI container factory class that adds the alerting context
    When the v1.10 alerting CDI migration executes
    Then the v1.10 alerting CDI migration migration is skipped

  Scenario: The migration is skipped if the project does not depend on alerting
    Given a project that does not depend on `foundation-alerting`
    And the default CDI container factory class that does not add the alerting context
    When the v1.10 alerting CDI migration executes
    Then the v1.10 alerting CDI migration migration is skipped
