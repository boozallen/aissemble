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

import java.io.File;

import com.boozallen.aissemble.upgrade.migration.AbstractMigrationTest;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class SparkPipelineMessagingPomMigrationSteps extends AbstractMigrationTest {
    private static String TEST_DIR = "v1_10_0/SparkPipelineMessagingMigration/migration/";
    private String dependency;

    @Given("a POM with the {string} packaging type")
    public void a_pom_with_the_packaging_type(String packaging) {
        if (!packaging.equals("jar")) {
            this.testFile = getTestPom("non-jar/pom.xml");
        }
    }

    @Given("a {string} dependency")
    public void a_dependency(String dependency) {
        this.dependency = dependency;
        if (dependency.equals("smallrye-reactive-messaging-kafka")) {
            this.testFile = getTestPom("smallrye-kafka/pom.xml");
        } else {
            this.testFile = getTestPom("smallrye-non-kafka/pom.xml");
        }
    }

    @Given("the pipeline module pom has already been migrated")
    public void the_pipeline_module_pom_has_already_been_migrated() {
        if (dependency.equals("smallrye-reactive-messaging-kafka")) {
            this.testFile = getTestFile("v1_10_0/SparkPipelineMessagingMigration/validation/smallrye-kafka/pom.xml");
        } else {
            this.testFile = getTestFile("v1_10_0/SparkPipelineMessagingMigration/validation/smallrye-non-kafka/pom.xml");
        }
    }

    @Given("no smallrye dependencies")
    public void no_smallrye_dependencies() {
        this.testFile = getTestPom("non-smallrye/pom.xml");
    }

    @When("the Spark Pipeline Messaging Pom migration executes")
    public void the_spark_pipeline_messaging_pom_migration_executes() {
        performMigration(new SparkPipelineMessagingPomMigration());
    }

    @Then("the aissemble kafka messaging dependency is added to the POM")
    public void the_aissemble_kafka_messaging_dependency_is_added_to_the_pom() {
        assertMigrationSuccess();
        assertTestFileMatchesExpectedFile("aissemble kafka messaging dependency was not added to the POM");
    }

    @Then("the Spark Pipeline Messaging Pom migration is skipped")
    public void the_spark_pipeline_messaging_pom_migration_is_skipped() {
        assertMigrationSkipped();
    }

    protected File getTestPom(String pomName) {
        return getTestFile(TEST_DIR + pomName);
    }
}
