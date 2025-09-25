package com.boozallen.aissemble.upgrade.migration.version_specific;

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

public class Poetry2IncludeMigrationSteps extends AbstractMigrationTest {
    @Given("a pyproject.toml file that uses includes to package Fermenter-generated files")
    public void aPyprojectTomlFileThatUsesIncludesToPackageFermenterGeneratedFiles() {
        setTestFileToVersionMigration("Poetry2IncludeMigration", "pyproject.toml");
    }

    @When("the poetry 2 include migration is executed")
    public void thePoetryIncludeMigrationIsExecuted() {
        Poetry2IncludeMigration migration = new Poetry2IncludeMigration();
        performMigration(migration);
    }

    @Then("the configuration is updated to explicitly list the formats")
    public void theConfigurationIsUpdatedToExplicitlyListTheFormats() {
        assertMigrationSuccess();
        assertTestFileMatchesExpectedFile("The pyproject.toml file is not updated correctly");
    }
}
