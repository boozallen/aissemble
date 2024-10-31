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

public class SparkPipelineServletApiMigrationSteps extends AbstractMigrationTest {
    @Given("a POM with {string} packaging type")
    public void a_pom_with_packaging_type(String packaging) {
        if (!packaging.equals("jar")) {
            testFile = getTestFile("v1_10_0/SparkPipelineServletApiMigration/skip/pom.xml");
        }
    }

    @Given("the POM does not contain the javax servlet-api dependency")
    public void the_pom_does_not_contain_the_javax_servlet_api_dependency() {
        testFile = getTestFile("v1_10_0/SparkPipelineServletApiMigration/migration/pom.xml");
    }

    @Given("the POM already contains the javax servlet-api dependency")
    public void the_pom_already_contains_the_javax_servlet_api_dependency() {
        testFile = getTestFile("v1_10_0/SparkPipelineServletApiMigration/validation/pom.xml");
    }

    @When("the Spark Pipeline Servlet API migration executes")
    public void the_spark_pipeline_servlet_api_migration_executes() {
        performMigration(new SparkPipelineServletApiMigration());
    }

    @Then("the javax servlet-api dependency is added to the POM")
    public void the_javax_servlet_api_dependency_is_added_to_the_pom() {
        assertMigrationSuccess();
        assertTestFileMatchesExpectedFile("javax servlet-api dependency was not added to the POM");
    }

    @Then("the Spark Pipeline Servlet API migration is skipped")
    public void the_spark_pipeline_servlet_api_migration_is_skipped() {
        assertMigrationSkipped();
    }
}
