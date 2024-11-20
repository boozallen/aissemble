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
import com.boozallen.aissemble.upgrade.migration.extensions.TestAlertingCdiMigration;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;

import java.util.List;

public class AlertingCdiMigrationSteps extends AbstractMigrationTest {
    private final MavenProject project = new MavenProject();

    @Given("a project that depends on `foundation-alerting`")
    public void aProjectThatDependsOnFoundationAlerting() {
        Dependency foundationAlerting = new Dependency();
        foundationAlerting.setGroupId("com.boozallen.aissemble");
        foundationAlerting.setArtifactId("foundation-alerting");
        foundationAlerting.setVersion("1.10.0");
        project.setDependencies(List.of(foundationAlerting));
    }

    @Given("a project that does not depend on `foundation-alerting`")
    public void aProjectThatDoesNotDependOnFoundationAlerting() {
    }

    @Given("the default CDI container factory class that does not add the alerting context")
    public void theDefaultCDIContainerFactoryClassWithoutAlerting() {
        testFile = getTestFile("v1_10_0/AlertingCdiMigration/migration/CdiContainerFactory.java");
    }

    @Given("the default CDI container factory class that adds the alerting context")
    public void theDefaultCDIContainerFactoryClassWithAlerting() {
        testFile = getTestFile("v1_10_0/AlertingCdiMigration/validation/CdiContainerFactory.java");
    }

    @When("the v1.10 alerting CDI migration executes")
    public void theAlertingCDIMigrationExecutes() {
        TestAlertingCdiMigration migration = new TestAlertingCdiMigration(project);
        performMigration(migration);
    }

    @Then("the file is migrated successfully")
    public void theFileIsMigratedSuccessfully() {
        assertMigrationSuccess();
        assertTestFileMatchesExpectedFile("CDIContainerFactory not migrated correctly");
    }

    @Then("the v1.10 alerting CDI migration migration is skipped")
    public void theAlertingCDIMigrationMigrationIsSkipped() {
        assertMigrationSkipped();
    }
}
