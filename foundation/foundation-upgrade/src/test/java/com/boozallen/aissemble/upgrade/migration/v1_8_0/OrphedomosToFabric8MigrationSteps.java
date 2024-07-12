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
import org.technologybrewery.baton.util.pom.PomHelper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

public class OrphedomosToFabric8MigrationSteps  extends AbstractMigrationTest {

    protected List<String> profilePluginsArtifactIds;

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
        Object configuration = model.getBuild().getPlugins().get(1).getConfiguration();
        assertNotNull("The configuration was not added properly", configuration);
    }
    @Then("the previous configuration was removed")
    public void the_previous_configuration_was_removed() {
        Model model = PomHelper.getLocationAnnotatedModel(testFile);
        boolean hasOrphedomosArtifactId = model.getBuild().getPluginManagement().getPlugins().stream()
                .map(Plugin::getArtifactId)
                .anyMatch(artifactId -> artifactId.equals("orphedomos-maven-plugin"));
        assertFalse("The previous orphedomos plugin was not removed successfully", hasOrphedomosArtifactId);
    }


    @Given("A pom with its packaging set to Orphedomos")
    public void a_pom_with_its_packaging_set_to_orphedomos() {
        testFile = getTestFile("v1_8_0/OrphedomosToFabric8Migration/migrate-packaging-only/pom.xml");
    }
    @Then("The pom is updated to use packaging type of docker-build")
    public void the_pom_is_updated_to_use_packaging_type_of_docker_build() {
        Model model = PomHelper.getLocationAnnotatedModel(testFile);
        Object packaging = model.getPackaging();
        assertEquals("docker-build", packaging);
    }

    @When("The migration is executed")
    public void the_migration_is_executed() {
        performMigration(new OrphedomosToFabric8Migration());

    }

    @Given("A pom with an Orphedomos config in a {string} profile")
    public void a_pom_with_an_orphedomos_config_in_a_profile(String profile) {
        testFile = getTestFile(String.format("v1_8_0/OrphedomosToFabric8Migration/migrate-profile/%s/pom.xml", profile));
    }

    @Then("Fabric8 is configured properly in the {string} profile")
    public void fabric8_is_configured_properly_in_the_profile(String profileId) {
        Model model = PomHelper.getLocationAnnotatedModel(testFile);
        profilePluginsArtifactIds = model.getProfiles().stream()
                .filter(profile -> profile.getId().equals(profileId))
                .flatMap(profile -> profile.getBuild().getPlugins().stream())
                .map(Plugin::getArtifactId)
                .collect(Collectors.toCollection(ArrayList::new));
        assertTrue(String.format("Orphedomos should not be present in profile \"%s\".", profileId), profilePluginsArtifactIds.contains("docker-maven-plugin"));
    }

    @And("the previous {string} profile configuration was removed")
    public void the_previous_profile_configuration_was_removed(String profileId) {
        assertFalse(String.format("Orphedomos should not be present in profile \"%s\".", profileId), profilePluginsArtifactIds.contains("orphedomos-maven-plugin"));
    }
}
