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

import com.boozallen.aissemble.upgrade.migration.AbstractMigrationTest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class UpdateDataAccessThriftServerEndpointMigrationSteps extends AbstractMigrationTest {
    @Given("a project with data access enabled using the default thrift endpoint")
    public void a_project_with_data_access_enabled_using_the_default_thrift_endpoint() {
        testFile = getTestFile("v1_9_0/UpdateDataAccessThriftEndpoint/default-endpoint-found/pre-migration/application.properties");
    }

    @When("the 1.9.0 data access thrift endpoint migration executes")
    public void the_1_9_0_data_access_thrift_endpoint_migration_executes() {
        performMigration(new UpdateDataAccessThriftServerEndpointMigration());
    }

    @Then("the application.properties file will be updated to reference the new thrift endpoint")
    public void the_application_properties_file_will_be_updated_to_reference_the_new_thrift_endpoint() throws IOException {
        assertEquals(
                "Expected file contents to match.",
                Files.readString(testFile.toPath()),
                Files.readString(Paths.get(
                        "target",
                        "test-classes",
                        "test-files",
                        "v1_9_0/UpdateDataAccessThriftEndpoint/default-endpoint-found/post-migration/application.properties"
                ))
        );
    }

    @Given("a project with data access enabled using a non-default thrift endpoint")
    public void a_project_with_data_access_enabled_using_a_non_default_thrift_endpoint() {
        testFile = getTestFile("v1_9_0/UpdateDataAccessThriftEndpoint/nonstandard-endpoint-defined/pre-migration/application.properties");
    }

    @When("the 1.9.0 data access thrift endpoint migration determines whether to execute")
    public void the_1_9_0_data_access_thrift_endpoint_determines_whether_to_execute() {
        performMigration(new UpdateDataAccessThriftServerEndpointMigration());
    }

    @Then("the migration system shall skip migrating the file")
    public void the_migration_system_shall_skip_migrating_the_file() {
        assertFalse(shouldExecute);
    }
}
