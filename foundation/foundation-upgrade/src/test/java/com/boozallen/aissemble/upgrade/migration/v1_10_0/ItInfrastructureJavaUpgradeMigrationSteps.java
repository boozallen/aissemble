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

public class ItInfrastructureJavaUpgradeMigrationSteps extends AbstractMigrationTest {

    @Given("a project with outdated java docker image")
    public void aProjectWithOutdatedJavaDockerImage() {
        testFile = getTestFile("v1_10_0/ItInfrastructureUpgrade/migration/perform/Dockerfile");
    }

    @Given("a project that is using the java 17 docker image")
    public void aProjectThatIsUsingTheJavaDockerImage() {
        testFile = getTestFile("v1_10_0/ItInfrastructureUpgrade/migration/skip/Dockerfile");
    }

    @When("the it infrastructure migration executes")
    public void theItInfrastructureMigrationExecutes() {
        performMigration(new ItInfrastructureJavaUpgradeMigration());
    }

    @Then("the docker image being used is JDK 17")
    public void theDockerImageBeingUsedIsJDK() {
        assertMigrationSuccess();
        assertTestFileMatchesExpectedFile("Docker image was not updated to use the correct JDK image");
    }

    @Then("the migration was skipped")
    public void theDockerImageIsNotUpdated() {
        assertMigrationSkipped();
    }
}
