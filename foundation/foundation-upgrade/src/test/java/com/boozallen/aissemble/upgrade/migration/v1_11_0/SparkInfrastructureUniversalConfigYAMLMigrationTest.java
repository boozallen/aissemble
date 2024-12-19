package com.boozallen.aissemble.upgrade.migration.v1_11_0;

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


public class SparkInfrastructureUniversalConfigYAMLMigrationTest extends AbstractMigrationTest {
    @Given("default values.yaml of spark infrastructure")
    public void aSparkInfrastructureValuesFileWithDefaultValues()
    {
        testFile = getTestFile("v1_11_0/SparkInfrastructureUniversalConfigYAMLMigration/migration/default-values.yaml");

    }
    @Given("values.yaml of spark infrastructure with hive username and password changed from \"hive\"")
    public void aSparkInfrastructureValuesFileModifiedToCustomValues()
    {
        testFile = getTestFile("v1_11_0/SparkInfrastructureUniversalConfigYAMLMigration/migration/custom-values.yaml");

    }

    @When("the spark infrastructure configuration migration executes")
    public void theSparkInfrastructureConfigurationMigrationExecutes()
    {
        SparkInfrastructureUniversalConfigYAMLMigration migration = new SparkInfrastructureUniversalConfigYAMLMigration();
        performMigration(migration);
    }

    @Then("values.yaml is updated to remove hive username properties")
    public void theHiveUsernameRemoved() {
        assertMigrationSuccess();
        var updatedValue = getTestFile("v1_11_0/SparkInfrastructureUniversalConfigYAMLMigration/migration/updated-values.yaml");
        assertLinesMatch("Yaml file not updated with removed hive username properties: " + testFile.getName(), testFile, updatedValue);
    }

    @Then("spark infrastructure configuration migration is skipped")
    public void theSparkInfrastructureConfigMigrationIsSkipped() {
        assertMigrationSkipped();
    }
}
