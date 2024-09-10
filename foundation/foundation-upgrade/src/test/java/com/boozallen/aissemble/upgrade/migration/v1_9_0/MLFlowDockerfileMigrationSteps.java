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
import org.technologybrewery.baton.BatonException;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class MLFlowDockerfileMigrationSteps extends AbstractMigrationTest {
    private AbstractAissembleMigration migration;
    private File validatedFile;

    @Given("a Dockerfile is referencing the mlflow image from aissemble-mlflow")
    public void aDockerfileIsReferencingTheMlflowImageFromAissembleMlflow() {
        testFile = getTestFile("v1_9_0/MLFlowDockerfileMigration/migration/applicable-Dockerfile");
    }

    @Given("a Dockerfile that is NOT referencing the mlflow image from aissemble-mlflow")
    public void a_dockerfile_that_is_not_referencing_the_mlflow_image_from_aissemble_mlflow() {
        testFile = getTestFile("v1_9_0/MLFlowDockerfileMigration/migration/inapplicable-Dockerfile");
    }


    @When("the 1.9.0 MLFlow Docker image migration executes")
    public void theMLFlowDockerImageMigrationExecutes() {
        migration = new MLFlowDockerfileMigration();
        performMigration(migration);
    }

    @Then("the image will pull the community docker image")
    public void theImageWillPullTheCommunityDockerImage() {
        validatedFile = getTestFile("/v1_9_0/MLFlowDockerfileMigration/validation/applicable-Dockerfile");
        assertLinesMatch("Dockerfile is still referencing aissemble-mlflow instead of community mlflow Docker image.", validatedFile, testFile);
    }

    @Then("the image is unchanged")
    public void the_image_is_unchanged() {
        validatedFile = getTestFile("/v1_9_0/MLFlowDockerfileMigration/validation/inapplicable-Dockerfile");
        assertLinesMatch("The migration is processing Dockerfile it should NOT be migrating!", validatedFile, testFile);
    }
}
