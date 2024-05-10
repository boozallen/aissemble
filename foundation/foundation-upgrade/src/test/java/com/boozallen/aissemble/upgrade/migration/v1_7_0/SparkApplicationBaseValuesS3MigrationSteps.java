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

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class SparkApplicationBaseValuesS3MigrationSteps extends AbstractMigrationTest {
    
    String sparkConfig;
    String testCase;

    @Before
    public void setTestCase() {
        testCase = null;
        sparkConfig = null;
    }

    @Given("a project that has a spark application base values")
    public void a_project_that_has_a_spark_application_base_values() {
        // nothing to do here, handling setting the testFile below
    }

    @Given("both the base values executor and driver contain hardcoded Localstack S3 credentials")
    public void both_the_base_values_executor_and_driver_contain_hardcoded_localstack_S3_credentials() {
        // nothing to do here, handling setting the testFile below
    }

    @Given("only the base values {string} contain hardcoded Localstack S3 credentials")
    public void only_the_base_values_contain_hardcoded_localstack_S3_credentials(String sparkConfig) {
        this.sparkConfig = sparkConfig + "-";
    }

    @Given("the base values contains {string} configuration")
    public void the_base_values_contains_configuration(String yamlConfig) {
        this.testCase = this.sparkConfig == null ? yamlConfig : this.sparkConfig + yamlConfig;
        testFile = getTestFile("v1_7_0/SparkApplicationBaseValuesS3Migration/migration/" + this.testCase + "/test-pipeline-base-values.yaml");
    }

    @Given("the base values contains secret based S3 credentials")
    public void the_base_values_contains_secret_based_S3_credentials() throws IOException {
        testFile = getTestFile("v1_7_0/SparkApplicationBaseValuesS3Migration/skip-migration/base-values-secret-s3-cred/test-pipeline-base-values.yaml");
    }

    @Given("the base values does not contain any S3 credentials")
    public void the_base_values_does_not_contain_any_S3_credentials() throws IOException {
        testFile = getTestFile("v1_7_0/SparkApplicationBaseValuesS3Migration/skip-migration/base-values-no-s3-cred/test-pipeline-base-values.yaml");
    }

    @Given("the base values does not contain any environment variables")
    public void the_base_values_does_not_contain_any_environment_variables() throws IOException {
        testFile = getTestFile("v1_7_0/SparkApplicationBaseValuesS3Migration/skip-migration/base-values-no-env/test-pipeline-base-values.yaml");
    }

    @When("the spark application base values S3 migration executes")
    public void the_spark_application_base_values_S3_migration_executes() {
        performMigration(new SparkApplicationBaseValuesS3Migration());
    }

    @Then("the base values S3 credentials will be updated to use a secret reference")
    public void the_base_values_S3_credentials_will_be_updated_to_use_a_secret_reference() throws IOException {
        File validationFile = getTestFile("v1_7_0/SparkApplicationBaseValuesS3Migration/validation/" + this.testCase + "/test-pipeline-base-values.yaml");
        File migratedFile = getTestFile("v1_7_0/SparkApplicationBaseValuesS3Migration/migration/" + this.testCase + "/test-pipeline-base-values.yaml");

        assertTrue("The migrated file is different from the validation file", FileUtils.contentEqualsIgnoreEOL(migratedFile, validationFile, null));
        assertTrue("Migration did not complete successfully", successful);
    }

    @Then("the hardcoded Localstack S3 credentials will be removed")
    public void the_hardcoded_Localstack_S3_credentials_will_be_removed() {
        // nothing to do here, handled by the validation above
    }

    @Then("the spark application base values S3 migration is skipped")
    public void the_spark_application_base_values_S3_migration_is_skipped() {
        assertFalse("The migration should be skipped", shouldExecute);
    }
}