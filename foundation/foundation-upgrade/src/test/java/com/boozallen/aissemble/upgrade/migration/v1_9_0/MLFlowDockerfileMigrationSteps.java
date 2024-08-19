package com.boozallen.aissemble.upgrade.migration.v1_9_0;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.upgrade.migration.AbstractAissembleMigration;
import com.boozallen.aissemble.upgrade.migration.AbstractMigrationTest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.io.FileUtils;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class MLFlowDockerfileMigrationSteps extends AbstractMigrationTest {
    private AbstractAissembleMigration migration;
    private File validatedFile;

    private static Boolean validateMigration(File original, File migrated) {
        Boolean isMigrated = false;

        try {
            isMigrated =  FileUtils.contentEquals(original, migrated);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isMigrated;
    }

    @Given("a Dockerfile is referencing the mlflow image from aissemble-mlflow")
    public void aDockerfileIsReferencingTheMlflowImageFromAissembleMlflow() {
        testFile = getTestFile("v1_9_0/MLFlowDockerfileMigration/migration/Dockerfile");
    }

    @When("the 1.9.0 MLFlow Docker image migration executes")
    public void theMLFlowDockerImageMigrationExecutes() {
        migration = new MLFlowDockerfileMigration();
        performMigration(migration);
    }

    @Then("the image will pull the community docker image")
    public void theImageWillPullTheCommunityDockerImage() {
        validatedFile = getTestFile("/v1_9_0/MLFlowDockerfileMigration/validation/Dockerfile");
        assertTrue("Dockerfile is still referencing aissemble-mlflow instead of community mlflow Docker image.", validateMigration(testFile, validatedFile));
    }
}
