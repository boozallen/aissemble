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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class EnableMavenDockerBuildMigrationStep extends AbstractMigrationTest {
    private static final Logger logger = LoggerFactory.getLogger(EnableMavenDockerBuildMigrationStep.class);
    public static final String MIGRATION_ROOT = "v1_11_0/EnableMavenDockerBuildMigration/migration/%s/pom.xml";

    @Given("a docker module pom file with fabric8 pluginManagement and skip configuration defined")
    public void aDockerModulePomFileWithFabric8PluginAndSkipConfiguration() {
        testFile = getTestFile(String.format(MIGRATION_ROOT, "perform"));
    }

    @Given("a docker module pom file with fabric8 pluginManagement but no skip configuration defined")
    public void aDockerModulePomFileWithFabric8PluginButNoSkipConfiguration() {
        testFile = getTestFile(String.format(MIGRATION_ROOT, "skip"));
    }

    @When("the 1.11.0 enable maven docker build migration executes")
    public void enableMavenDockerBuildMigrationExecutes() {
        EnableMavenDockerBuildMigration migration = new EnableMavenDockerBuildMigration();
        performMigration(migration);
    }

    @Then("the fabric8 pluginManagement skip configuration is removed")
    public void theFabric8PluginManagementSkipConfigIsRemoved() {
        String file = testFile.getParent().replaceAll("^.+/migration", "") + "/" + testFile.getName();
        File validatedFile = getTestFile("/v1_11_0/EnableMavenDockerBuildMigration/validation/" + file);
        assertLinesMatch("The fabric8 pluginManagement skip configuration is removed.", validatedFile, testFile);
    }

    @Then("docker module pom file migration is skipped")
    public void theMigrationIsSkipped() {
        assertMigrationSkipped();
    }
}
