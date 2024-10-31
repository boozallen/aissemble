package com.boozallen.aissemble.upgrade.migration.v1_10_0;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.upgrade.migration.AbstractMigrationTest;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class JavaPackageMigrationSteps extends AbstractMigrationTest {
    @Given("a Java file with the outdated Java package names")
    public void a_java_file_with_the_outdated_java_package_names() {
        testFile = getTestFile("v1_10_0/JavaPackageMigration/migration/allClasses.java");
    }

    @Given("a Java file with the new Java package names")
    public void a_java_file_with_the_mew_java_package_names() {
        testFile = getTestFile("v1_10_0/JavaPackageMigration/validation/allClasses.java");
    }

    @When("the Java Package Migration executes")
    public void the_java_package_migration_executes() {
        performMigration(new JavaPackageMigration());
    }

    @Then("the Java file is updated to use the new Java package names")
    public void the_java_file_is_updated_to_use_the_new_java_package_names() {
        assertMigrationSuccess();
        assertTestFileMatchesExpectedFile("Java file was not updated to use the new class packages");
    }

    @Then("the Java Package Migration is skipped")
    public void the_java_package_migration_is_skipped() {
        assertMigrationSkipped();
    }

}
