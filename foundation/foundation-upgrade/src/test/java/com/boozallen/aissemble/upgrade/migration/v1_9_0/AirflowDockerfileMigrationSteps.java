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

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AirflowDockerfileMigrationSteps extends AbstractMigrationTest {
    private AbstractAissembleMigration migration;
    private File validatedFile;

    @Given("a Dockerfile is referencing the airflow image from aissemble-airflow")
    public void aDockerfileIsReferencingTheAirflowImageFromAissembleAirflow() {
        testFile = getTestFile("v1_9_0/AirflowDockerfileMigration/migration/Dockerfile");
    }

    @Given("a Docker file is referencing the community airflow image")
    public void aDockerFileIsReferencingTheCommunityAirflowImage() {
        testFile = getTestFile("v1_9_0/AirflowDockerfileMigration/migration/negative-Dockerfile");
    }

    @Given("a Docker file is not referencing Airflow")
    public void aDockerFileIsNotReferencingAirflow() {
        testFile = getTestFile("v1_9_0/AirflowDockerfileMigration/migration/negative-non-Airflow-Dockerfile");
    }

    @When("the 1.9.0 Airflow Docker image migration executes")
    public void theAirflowDockerImageMigrationExecutes() {
        migration = new AirflowDockerfileMigration();
        performMigration(migration);
    }

    @Then("the Dockerfile will pull the community docker Airflow image")
    public void theDockerfileWillPullTheCommunityDockerAirflowImage() {
        validatedFile = getTestFile("/v1_9_0/AirflowDockerfileMigration/validation/Dockerfile");
        assertTrue("Dockerfile is still referencing aissemble-airflow instead of community airflow Docker image.", validateMigration(testFile, validatedFile));
    }

    @Then("the airflow migration is skipped")
    public void theAirflowMigrationIsSkipped() {
        assertFalse("The migration is processing a Dockerfile that does NOT require a migration!", shouldExecute);
    }
}
