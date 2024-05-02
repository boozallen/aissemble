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

import com.boozallen.aissemble.upgrade.migration.AbstractMigrationTest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DockerPipInstallMigrationSteps extends AbstractMigrationTest {

    @Given("a project that has a dockerfile with at least one instruction that encompasses multiple installs")
    public void a_project_that_has_a_dockerfile_with_at_least_one_instruction_that_encompasses_multiple_installs() {
        testFile = getTestFile("v1_7_0/DockerPipInstallMigration/migration/dockerfile");
    }

    @When("the docker migration executes")
    public void the_docker_migration_executes() {
        performMigration(new DockerPipInstallMigration());
    }

    @Then("the dockerfile exhibits the correct instructions")
    public void the_dockerfile_exhibits_the_correct_instructions() throws IOException {
        File validationFile = getTestFile("v1_7_0/DockerPipInstallMigration/validation/dockerfile");
        File migratedFile = getTestFile("v1_7_0/DockerPipInstallMigration/migration/dockerfile");

        assertTrue(
                "Migrated file and validation file are different",
                FileUtils.contentEqualsIgnoreEOL(migratedFile, validationFile, null)
        );
        assertTrue("Migration did not complete successfully", successful);
    }

    @Given("a project that has a dockerfile with no instructions that encompasses multiple installs")
    public void a_project_that_has_a_dockerfile_with_no_instructions_that_encompasses_multiple_installs() {
        testFile = getTestFile("v1_7_0/MlflowV2ExternalS3Migration/skip-migration/dockerfile");
    }

    @Then("the docker migration is skipped")
    public void the_docker_migration_is_skipped() {
        assertFalse("The migration should be skipped", shouldExecute);
    }


}
