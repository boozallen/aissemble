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

import com.boozallen.aissemble.upgrade.migration.AbstractMigrationTest;
import com.boozallen.aissemble.upgrade.util.pom.PomHelper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.maven.model.Model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

public class OrphedomosToFabric8MigrationSteps  extends AbstractMigrationTest {

    @Given("A pom with Orphedomos in the plugin management")
    public void a_pom_with_orphedomos_in_the_plugin_management() {
        testFile = getTestFile("v1_8_0/OrphedomosToFabric8Migration/migrate-configuration/pom.xml");
    }
    @When("The migration executes")
    public void the_migration_executes() {
        performMigration(new OrphedomosToFabric8Migration());
        assertTrue("The migration should execute", shouldExecute);
    }
    @Then("The pom is updated to use the Fabric8 docker-maven-plugin")
    public void the_pom_is_updated_to_use_the_fabric8_docker_maven_plugin() {
        Model model = PomHelper.getLocationAnnotatedModel(testFile);
        Object artifactId = model.getBuild().getPlugins().get(1).getArtifactId();
        assertEquals("docker-maven-plugin", artifactId.toString());

        Object groupId = model.getBuild().getPlugins().get(1).getGroupId();
        assertEquals("io.fabric8", groupId.toString());

    }

    @And("Fabric8 is configured properly")
    public void fabric8_is_configured_properly(){
        Model model = PomHelper.getLocationAnnotatedModel(testFile);
        Object executions = model.getBuild().getPlugins().get(1).getExecutions();
        assertNotNull("The executions were not added properly", executions);
    }
    @Then("the previous configuration was removed")
    public void the_previous_configuration_was_removed() {
        Model model = PomHelper.getLocationAnnotatedModel(testFile);
        Object configuration = model.getBuild().getPlugins().get(1).getConfiguration();
        assertNull("The previous configuration was not removed successfully", configuration);
    }


    @Given("A pom with its packaging set to Orphedomos")
    public void a_pom_with_its_packaging_set_to_orphedomos() {
        testFile = getTestFile("v1_8_0/OrphedomosToFabric8Migration/migrate-packaging/pom.xml");
    }
    @Then("The pom is updated to use packaging type of docker-build")
    public void the_pom_is_updated_to_use_packaging_type_of_docker_build() {
        Model model = PomHelper.getLocationAnnotatedModel(testFile);
        Object packaging = model.getPackaging();
        assertEquals("docker-build", packaging);
    }

    @Given("A pom within the tests docker module")
    public void a_pom_within_the_tests_docker_module() {
        testFile = getTestFile("v1_8_0/OrphedomosToFabric8Migration/skip-migration/pom.xml");
    }
    @When("The migration is executed")
    public void the_migration_is_executed() {
        performMigration(new OrphedomosToFabric8Migration());

    }
    @Then("The pom is not updated to use the Fabric8 docker-maven-plugin")
    public void the_pom_is_not_updated_to_use_the_fabric8_docker_maven_plugin() {
        assertFalse("The migration should be skipped", shouldExecute);
    }

}
