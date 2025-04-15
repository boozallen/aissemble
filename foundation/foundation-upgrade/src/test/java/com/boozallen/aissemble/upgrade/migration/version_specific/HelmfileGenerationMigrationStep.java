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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import com.boozallen.aissemble.upgrade.migration.AbstractMigrationTest;
import com.boozallen.aissemble.upgrade.migration.extensions.HelmfileGenerationMigrationTest;

import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class HelmfileGenerationMigrationStep extends AbstractMigrationTest {

    @After("@helmfile-generate")
    public void setup() {
        // Clean up created helmfile
        File helmfile = getTestFile(Path.of("version-specific", "HelmfileGenerationMigration", "migration",
                "helmfile.yaml").toString());
        helmfile.delete();
    }

    @Given("a projects does not have a helm file")
    public void aProjectsDoesNotHaveAHelmFile() {
        setTestFileToVersionMigration("HelmfileGenerationMigration", "pom.xml");
    }

    @Given("the system property `aissemble.enable.helmfile.migration` is set")
    public void theSystemPropertyActivationKeyIsSet() {
        System.setProperty("aissemble.enable.helmfile.migration", "true");
    }

    @Given("a projects has a helm file")
    public void aProjectsHasAHelmFile() throws IOException {
        setTestFileToVersionMigration("HelmfileGenerationMigration", "pom.xml");
        File helmfile = getTestFile(Path.of("version-specific", "HelmfileGenerationMigration", "migration",
                "helmfile.yaml").toString());
        helmfile.createNewFile();
    }

    @Given("the system property `aissemble.enable.helmfile.migration` is not set")
    public void theSystemPropertyActivationKeyIsNotSet() {
        setTestFileToVersionMigration("HelmfileGenerationMigration", "pom.xml");
        System.clearProperty("aissemble.enable.helmfile.migration");
    }

    @When("the helmfile generation migration is performed")
    public void theHelmfileGenerationMigrationIsPerformed() {
        performMigration(new HelmfileGenerationMigrationTest());
    }


    @And("the helmfile is generated")
    public void theHelmfileIsGenerated() {
        File helmfile = getTestFile(Path.of("version-specific", "HelmfileGenerationMigration", "migration",
                "helmfile.yaml").toString());
        assertTrue("Helmfile was not generated.", helmfile.exists());
    }

    @Then("the helmfile was not changed")
    public void theHelmfileWasNotChanged() {
        File helmfile = getTestFile(Path.of("version-specific", "HelmfileGenerationMigration", "migration",
                "helmfile.yaml").toString());
        assertTrue("Helmfile was not found. It should not have been deleted.", helmfile.exists());
        assertEquals("Helmfile was modified when it should not have been", 0L, helmfile.length());
    }

    @Then("the helmfile generation is skipped")
    public void theHelmGenerationIsSkipped() {
        assertMigrationSkipped();
    }
}
