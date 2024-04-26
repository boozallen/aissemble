package com.boozallen.aissemble.upgrade.migration.v1_7_0;
/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import com.boozallen.aissemble.upgrade.migration.AbstractMigrationTest;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class SparkMemoryUpgradeMigrationSteps extends AbstractMigrationTest {

    private SparkMemoryUpgradeMigration sparkmigration;
    @Given("a project that has a spark application")
    public void a_project_that_has_spark_application() {
        assertTrue("The project has a Spark application", true);
    }

    @Given("the base value contains the old memory value")
    public void the_values_yaml_has_old_memory_value() throws IOException {
        testFile = getTestFile("v1_7_0/SparkUpgradeMigration/migration/base-values.yaml");
    }

    @When("the 1.7.0 spark application memory migration executes")
    public void the_value_yaml_migration_executes() {
        performMigration(new SparkMemoryUpgradeMigration());
    }
    
    @Then("the memory is updated to new value")
    public void the_values_dev_yaml_get_updated() throws IOException {
        File validationFile = getTestFile("v1_7_0/SparkUpgradeMigration/validation/base-values.yaml");
        File migratedFile = getTestFile("v1_7_0/SparkUpgradeMigration/migration/base-values.yaml");
        assertTrue("The content of the migrated file does not match the validation file",
                FileUtils.contentEqualsIgnoreEOL(migratedFile, validationFile, null));
        assertTrue("Migration did not complete successfully", successful);
    }

    @Given("the base values.yaml does not have default memory values")
    public void the_values_yaml_has_non_default_memory_value() throws IOException {
        testFile = getTestFile("v1_7_0/SparkUpgradeMigration/skip-migration/base-values.yaml");
    }
    @Then("the spark application memory migration is skipped")
    public void the_values_dev_yaml_unchanged() throws IOException {
        assertFalse("The migration should be skipped", shouldExecute);
    }
}
