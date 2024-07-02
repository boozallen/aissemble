package com.boozallen.aissemble.upgrade.migration.v1_8_0;

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
import org.technologybrewery.baton.util.pom.PomHelper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.w3c.dom.Node;
import org.apache.maven.model.Model;

import static org.junit.Assert.*;

public class DisablePythonLintingMigrationSteps extends AbstractMigrationTest {
    @Given("A root pom without Habushu in the plugin management")
    public void a_root_pom_without_habushu_is_in_the_plugin_management() {
        testFile = getTestFile("v1_8_0/DisablePythonLinting/migration/add-habushu-artifact/pom.xml");
    }

    @Given("A root pom with Habushu in the plugin management and does not contain the configuration tag")
    public void a_root_pom_with_habushu_is_in_the_plugin_management_and_does_not_contain_the_configuration_tag() {
        testFile = getTestFile("v1_8_0/DisablePythonLinting/migration/add-configuration/pom.xml");
    }

    @Given("A root pom with Habushu in the plugin management and does not contain both sourceFailOnLintErrors and testFailOnLintErrors")
    public void a_root_pom_with_habushu_is_in_the_plugin_management_and_does_not_contain_both_sourceFailOnLintErrors_and_testFailOnLintErrors() {
        testFile = getTestFile("v1_8_0/DisablePythonLinting/migration/add-linting/pom.xml");
    }
    @Given("A root pom with Habushu in the plugin management and contains either sourceFailOnLintErrors or testFailOnLintErrors")
    public void a_root_pom_with_habushu_is_in_the_plugin_management_and_contains_either_sourceFailOnLintErrors_and_testFailOnLintErrors() {
        testFile = getTestFile("v1_8_0/DisablePythonLinting/migration/linting-exists/pom.xml");
    }
    @When("The 1.8.0 python linting migration executes")
    public void the_1_8_0_python_linting_migration_executes() {
        performMigration(new PythonLintingMigration());
    }
    @Then("The root pom is updated with Habushu in plugin management")
    public void the_root_pom_file_is_updated_with_habushu_in_plugin_management() {
        assertTrue(shouldExecute);
    }

    @Then("Habushu is configured with sourceFailOnLintErrors and testFailOnLintErrors disabled")
    public void habushu_is_configured_with_sourceFailOnLintErrors_and_testFailOnLintErorrs_disabled() {
        Model model = PomHelper.getLocationAnnotatedModel(testFile);
        Object configuration = model.getBuild().getPluginManagement().getPlugins().get(1).getConfiguration();
        assertTrue(configuration.toString().contains("sourceFailOnLintErrors") && configuration.toString().contains("testFailOnLintErrors"));
    }

    @Then("The root pom is updated with the configuration tag")
    public void the_root_pom_is_updated_with_the_configuration_tag() {
        Model model = PomHelper.getLocationAnnotatedModel(testFile);
        Object configuration = model.getBuild().getPluginManagement().getPlugins().get(1).getConfiguration();
        assertNotNull(configuration);
    }

    @Then("The 1.8.0 python linting migration is skipped") 
    public void the_1_8_0_python_linting_migration_is_skipped() {
        assertFalse(shouldExecute);
    }
}