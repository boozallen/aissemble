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

public class SparkVersionUpgradeMigrationSteps extends AbstractMigrationTest {

    @Given("a project with outdated spark application configuration")
    public void aProjectWithOutdatedSparkApplicationConfiguration() {
        testFile = getTestFile("v1_10_0/SparkVersionUpgrade/migration/outdatedApps/base-values.yaml");
    }

    @Given("a project with up to date spark application configuration")
    public void aProjectWithUpToDateSparkApplicationConfiguration() {
        testFile = getTestFile("v1_10_0/SparkVersionUpgrade/migration/updatedApps/base-values.yaml");
    }

    @When("the spark version upgrade migration executes")
    public void theSparkVersionUpgradeMigrationExecutes() {
        performMigration(new SparkVersionUpgradeMigration());
    }

    @Then("the spark application configs are updated")
    public void theSparkApplicationConfigsAreUpdated() {
        assertMigrationSuccess();
        assertTestFileMatchesExpectedFile("Spark configs were not updated correctly following version migration");
    }

    @Then("the spark application configs are not updated")
    public void theSparkApplicationConfigsAreNotUpdated() {
        assertMigrationSkipped();
        assertTestFileMatchesExpectedFile("Spark configs were not updated correctly following version migration");
    }
}
