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

import com.boozallen.aissemble.upgrade.migration.AbstractAissembleMigration;
import com.boozallen.aissemble.upgrade.migration.AbstractMigrationTest;
import com.boozallen.aissemble.upgrade.migration.utils.MigrationTestUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.io.FileUtils;
import org.apache.maven.project.MavenProject;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertFalse;

public class ProjectSpecificImageNameMigrationSteps extends AbstractMigrationTest {
    private AbstractAissembleMigration migration;

    @Given("a new downstream maven project {string}")
    public void a_new_downstream_maven_project(String testProjectName) {
        MavenProject testProject = MigrationTestUtils.createTestMavenProject(testProjectName);
        migration = new ProjectSpecificImageNameMigration();
        migration.setMavenProject(testProject);
    }

    @Given("a values file with the older project specific image naming convention")
    public void a_values_file_with_the_older_project_specific_image_naming_convention() {
        testFile = getTestFile("/v1_7_0/ProjectSpecificImageNameMigration/migration/policy-decision-point/values.yaml");
    }

    @Given("a values file with a non project specific image")
    public void a_values_file_with_a_non_project_specific_image() {
        testFile = getTestFile("/v1_7_0/ProjectSpecificImageNameMigration/skip-migration/hive-metastore-db/values.yaml");
    }

    @When("the project specific image name change migration executes")
    public void the_project_specific_image_name_change_migration_executes() {
        performMigration(migration);
    }

    @Then("the values file is updated to use new project specific image naming convention")
    public void the_values_file_is_updated_to_use_new_project_specific_image_naming_convention() throws IOException {
        File validatedFile = getTestFile("/v1_7_0/ProjectSpecificImageNameMigration/validation/policy-decision-point/values_validation.yaml");

        Assert.assertTrue(FileUtils.contentEqualsIgnoreEOL(testFile, validatedFile, null));
        Assert.assertTrue("Migration did not complete successfully", successful);
    }

    @Then("the values file project specific image name change migration is skipped")
    public void the_values_file_project_specific_image_name_change_migration_is_skipped() {
        assertFalse("Values file does not use project specific image naming convention", shouldExecute);
    }

}