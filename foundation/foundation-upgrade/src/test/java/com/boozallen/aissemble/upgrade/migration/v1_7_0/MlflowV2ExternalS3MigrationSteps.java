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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.boozallen.aissemble.upgrade.migration.AbstractMigrationTest;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class MlflowV2ExternalS3MigrationSteps extends AbstractMigrationTest {
    private File chartYaml;
    private String testConfig;

    @Given("a project that has a mlflow v2 deployment")
    public void a_project_that_has_a_mlflow_v2_deployment() {
        this.chartYaml = getTestFile("v1_7_0/MlflowV2ExternalS3Migration/migration/Chart.yaml");
    }

    @Given("the values.yaml and values-dev.yaml do not contain {string} configuration")
    public void the_values_yaml_and_values_dev_yaml_do_not_contain_configuration(String yamlConfig) throws IOException {
        // set this parameter so we can access it later in validation
        this.testConfig = yamlConfig;

        // Copy the mlflow Chart.yaml to the appropriate directory for this test
        String testDirectory = "v1_7_0/MlflowV2ExternalS3Migration/migration/" + yamlConfig;
        FileUtils.copyFileToDirectory(chartYaml, getTestFile(testDirectory));

        testFile = getTestFile(testDirectory + "/Chart.yaml");
    }

    @When("the mlflow yaml migration executes")
    public void the_mlflow_yaml_migration_executes() {
        performMigration(new MlflowV2ExternalS3Migration());
    }
    
    @Then("the hardcoded Localstack credentials are added to the values-dev.yaml")
    public void the_hardcoded_localstack_credentials_are_added_to_the_values_dev_yaml() throws IOException {
        File validationFile = getTestFile("v1_7_0/MlflowV2ExternalS3Migration/validation/" + this.testConfig + "/values-dev.yaml");
        File migratedFile = getTestFile("v1_7_0/MlflowV2ExternalS3Migration/migration/" + this.testConfig + "/values-dev.yaml");

        assertTrue(FileUtils.contentEqualsIgnoreEOL(migratedFile, validationFile, null));
        assertTrue("Migration did not complete successfully", successful);
    }

    @Then("the Localstack secret credentials are added to the values.yaml")
    public void the_localstack_secret_credentials_are_added_to_the_values_yaml() throws IOException {
        File validationFile = getTestFile("v1_7_0/MlflowV2ExternalS3Migration/validation/" + this.testConfig + "/values.yaml");
        File migratedFile = getTestFile("v1_7_0/MlflowV2ExternalS3Migration/migration/" + this.testConfig + "/values.yaml");

        assertTrue(FileUtils.contentEqualsIgnoreEOL(migratedFile, validationFile, null));
        assertTrue("Migration did not complete successfully", successful);
    }

    @Given("the {string} contains external S3 configuration")
    public void the_contains_external_s3_credentials(String file) throws IOException {
        // Copy the mlflow Chart.yaml to the appropriate directory for this test
        String testDirectory = "v1_7_0/MlflowV2ExternalS3Migration/skip-migration/" + file;
        FileUtils.copyFileToDirectory(chartYaml, getTestFile(testDirectory));

        testFile = getTestFile(testDirectory + "/Chart.yaml");
    }

    @Then("the mlflow yaml migration is skipped")
    public void the_mlflow_yaml_migration_is_skipped() {
        assertFalse("The migration should be skipped", shouldExecute);
    }

    @Given("a project that does not contain any helm dependencies")
    public void a_project_that_does_not_contain_any_helm_dependencies() {
        testFile = getTestFile("v1_7_0/MlflowV2ExternalS3Migration/skip-migration/no-dependency/Chart.yaml");
    }

    @Given("a project that does not contain a mlflow v2 dependency")
    public void a_project_that_does_not_contain_a_mlflow_v2_dependency() {
        testFile = getTestFile("v1_7_0/MlflowV2ExternalS3Migration/skip-migration/no-mlflow-dependency/Chart.yaml");
    }
}
