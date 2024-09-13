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
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import io.cucumber.java.en.Given;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Profile;
import org.technologybrewery.baton.util.pom.PomHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class OrphedomosToFabric8MigrationSteps  extends AbstractMigrationTest {
    public static final String FABRIC8_ARTIFACT = "docker-maven-plugin";
    public static final String FABRIC8_GROUP = "${group.fabric8.plugin}";
    private File validationFile;

    @Given("a the default POM for the parent module of a project's Docker moduels")
    public void aTheDefaultPOMForTheParentModuleOfAProjectsDockerModuels() {
        testFile = getTestFile("v1_8_0/OrphedomosToFabric8Migration/migration/standard-parent/pom.xml");
        validationFile = getTestFile("v1_8_0/OrphedomosToFabric8Migration/validation/standard-parent/pom.xml");
    }

    @Given("a default POM for a Docker module")
    public void aDefaultPOMForADockerModule() {
        testFile = getTestFile("v1_8_0/OrphedomosToFabric8Migration/migration/standard-image/pom.xml");
        validationFile = getTestFile("v1_8_0/OrphedomosToFabric8Migration/validation/standard-image/pom.xml");
    }

    @Given("a POM that uses Orphedomos in multiple profiles")
    public void aPOMThatUsesOrphedomosInMultipleProfiles() {
        testFile = getTestFile("v1_8_0/OrphedomosToFabric8Migration/migration/multi-profile/pom.xml");
        validationFile = getTestFile("v1_8_0/OrphedomosToFabric8Migration/validation/multi-profile/pom.xml");
    }

    @Given("a POM that does not use the Orphedomos plugin")
    public void aPOMThatDoesNotUseTheOrphedomosPlugin() {
        testFile = getTestFile("v1_8_0/OrphedomosToFabric8Migration/migration/skip-migrate/pom.xml");
        validationFile = getTestFile("v1_8_0/OrphedomosToFabric8Migration/validation/skip-migrate/pom.xml");
    }

    @When("the Fabric8 migration executes")
    public void theMigrationExecutes() {
        performMigration(new OrphedomosToFabric8Migration());
    }

    @Then("the Orphedomos plugin is replaced with Fabric8")
    public void theOrphedomosPluginIsReplacedWithFabric() throws IOException {
        assertValidationFile("Orphedomos plugin should be replaced with Fabric8");
    }

    @Then("the Orphedomos packaging is replaced with Fabric8's docker-build packaging")
    public void theOrphedomosPackagingIsReplacedWithFabric8sDockerBuildPackaging() throws IOException {
        assertValidationFile("Orphedomos packaging should be replaced with `docker-maven-plugin`");
    }

    @Then("the Orphedomos plugin is replaced with Fabric8 in all profiles")
    public void theOrphedomosPluginIsReplacedWithFabricInAllProfiles() throws IOException {
        assertValidationFile("Orphedomos plugin in profiles should be replaced with Fabric8");
    }

    @Then("the {string} profile configuration is updated")
    public void theProfileConfigurationIsUpdated(String profile) {
        Model expectedModel = PomHelper.getLocationAnnotatedModel(validationFile);
        Model actualModel = PomHelper.getLocationAnnotatedModel(testFile);
        BuildBase expectedProfile = getProfile(expectedModel, profile).getBuild();
        BuildBase actualProfile = getProfile(actualModel, profile).getBuild();

        assertEquals("The profile configuration should be updated",
                getFabric8PluginConfig(expectedProfile.getPlugins()),
                getFabric8PluginConfig(actualProfile.getPlugins()));

        if (expectedProfile.getPluginManagement() != null) {
            assertEquals("The profile configuration should be updated in pluginManagement",
                    getFabric8PluginConfig(expectedProfile.getPluginManagement().getPlugins()),
                    getFabric8PluginConfig(actualProfile.getPluginManagement().getPlugins()));
        }
    }

    @Then("the POM is not modified")
    public void thePOMIsNotModified() {
        assertFalse("Migration should not have executed", shouldExecute);
    }

    private void assertValidationFile(String message) throws IOException {
        String expectedContent = FileUtils.readFileToString(validationFile, "UTF-8");
        String actualContent = FileUtils.readFileToString(testFile, "UTF-8");
        assertEquals(message, expectedContent, actualContent);
    }

    private static Profile getProfile(Model model, String id) {
        for (Profile profile : model.getProfiles()) {
            if (profile.getId().equals(id)) {
                return profile;
            }
        }
        throw new RuntimeException("Profile not found: " + id);
    }

    private static String getFabric8PluginConfig(List<Plugin> plugins) {
        if (CollectionUtils.isEmpty(plugins)) {
            return null;
        }
        for (Plugin plugin : plugins) {
            if (FABRIC8_GROUP.equals(plugin.getGroupId()) && FABRIC8_ARTIFACT.equals(plugin.getArtifactId())) {
                return Objects.toString(plugin.getConfiguration());
            }
        }
        throw new RuntimeException("Fabric8 plugin not found");
    }
}
