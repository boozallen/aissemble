Feature: Updates the affected java classes from their old package name to their new package name

  Scenario: Migrate Java classes to use updated package
    Given a Java file with the outdated Java package names
    When the Java Package Migration executes
    Then the Java file is updated to use the new Java package names

  Scenario: Skip migration when all Java packages are already migrated
    Given a Java file with the new Java package names
    When the Java Package Migration executes
    Then the Java Package Migration is skipped