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

public class Log4jMavenShadePluginMigrationSteps extends AbstractMigrationTest {

    @Given("a Maven Shade Plugin instance with an outdated dependency on Log4j")
    public void a_maven_shade_plugin_instance_with_an_outdated_dependency_on_log4j() {
        testFile = getTestFile("v1_10_0/Log4jMavenShadeUpgrade/migration/perform/pom.xml");
    }

    @Given("a Maven Shade Plugin instance without a Log4j dependency")
    public void a_Maven_Shade_Plugin_instance_without_a_Log4j_dependency() {
        testFile = getTestFile("v1_10_0/Log4jMavenShadeUpgrade/migration/skip/pom.xml");
    }

    @When("the Log4j Maven Shade Plugin migration executes")
    public void the_log4j_maven_shade_plugin_migration_executes() {
        performMigration(new Log4jMavenShadePluginMigration());
    }
    
    @Then("the Maven Shade Plugin is updated to use the new Log4j dependency")
    public void the_maven_shade_plugin_is_updated_to_use_the_new_log4j_dependency() {
        assertMigrationSuccess();
        assertTestFileMatchesExpectedFile("Log4j dependency was not updated correctly following the migration");
    }

    @Then("the transformer configuration is updated with the new implementation")
    public void the_transformer_configuration_is_updated_with_the_new_implementation() {
        // handled by the above check
    }

    @Then("the Log4j Maven Shade Plugin migration is skipped")
    public void the_Log4j_Maven_Shade_Plugin_migration_is_skipped() {
        assertMigrationSkipped();
    }
}
