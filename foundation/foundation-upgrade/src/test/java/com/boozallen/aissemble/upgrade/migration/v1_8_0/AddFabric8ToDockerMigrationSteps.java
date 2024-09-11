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
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.File;

public class AddFabric8ToDockerMigrationSteps extends AbstractMigrationTest {
    private static final String MIGRATION_DIR = "v1_8_0/Fabric8LocationMigration/migration/";

    private String packaging;

    @Given("a POM with the {string} packaging type")
    public void aPomWithThePackagingType(String packaging) {
        this.packaging = packaging;
    }

    @Given("the docker-maven-plugin is not already in the build section")
    public void theDockerMavenPluginIsNotAlreadyInTheBuildSection() {
        testFile = getTestPom("missing-plugin-pom.xml");
    }

    @Given("there is no plugins section in build")
    public void thereIsNoPluginsSectionInBuild() {
        testFile = getTestPom("no-plugins-pom.xml");
    }

    @Given("there is no build section")
    public void thereIsNoBuildSection() {
        testFile = getTestPom("no-build-pom.xml");
    }

    @Given("the docker plugin is already in the build section")
    public void theDockerPluginIsAlreadyInTheBuildSection() {
        testFile = getTestPom("plugin-present-pom.xml");
    }

    @Given("the docker-maven-plugin is configured directly in the build")
    public void theDockerMavenPluginIsConfiguredDirectlyInTheBuild() {
        testFile = getTestPom("in-build-pom.xml");
    }

    @Given("the docker-maven-plugin is configured directly in the build of a profile")
    public void theDockerMavenPluginIsConfiguredDirectlyInTheBuildOfAProfile() {
        testFile = getTestPom("in-profile-pom.xml");
    }

    @Given("the docker-maven-plugin is the only plugin configured in the build")
    public void theDockerMavenPluginIsTheOnlyPluginConfiguredInTheBuild() {
        testFile = getTestPom("no-other-plugins-pom.xml");
    }

    @Given("the docker-maven-plugin is configured directly in the build but there is no pluginManagement section")
    public void theDockerMavenPluginIsConfiguredDirectlyInTheBuildButThereIsNoPluginManagementSection() {
        testFile = getTestPom("no-plugin-management-pom.xml");
    }

    @When("the add Fabric8 migration executes")
    public void theAddFabricMigrationExecutes() {
        performMigration(new AddFabric8ToDockerMigration());
    }

    @When("the Fabric8 location migration executes")
    public void theFabricConfigurationMigrationExecutes() {
        performMigration(new Fabric8LocationMigration());
    }

    @Then("the docker-maven-plugin is added to the build section with minimal configuration")
    public void theDockerMavenPluginIsAddedToTheBuildSectionWithMinimalConfiguration() {
        assertMigrationSuccess();
        assertTestFileMatchesExpectedFile("The docker plugin was not correctly added to the build section");
    }

    @Then("the docker-maven-plugin is added to a new build section with minimal configuration")
    public void theDockerMavenPluginIsAddedToANewBuildSectionWithMinimalConfiguration() {
        assertMigrationSuccess();
        assertTestFileMatchesExpectedFile("The docker plugin was not correctly added to a new build section");
    }

    @Then("the add Fabric8 migration is skipped")
    public void theAddFabricMigrationIsSkipped() {
        assertMigrationSkipped();
    }

    @Then("the plugin configuration is moved to the pluginManagement section")
    public void thePluginConfigurationIsMovedToThePluginManagementSection() {
        assertMigrationSuccess();
        assertTestFileMatchesExpectedFile("Failed to move the docker plugin to the pluginManagement section");
    }

    @Then("the plugin configuration is not moved to the pluginManagement section")
    public void thePluginConfigurationIsNotMovedToThePluginManagementSection() {
        assertMigrationSkipped();
    }

    @Then("the plugin configuration is moved to the pluginManagement section within the profile")
    public void thePluginConfigurationIsMovedToThePluginManagementSectionWithinTheProfile() {
        assertMigrationSuccess();
        assertTestFileMatchesExpectedFile("Failed to move the docker plugin within a profile");
    }

    @Then("the plugin configuration is moved to the pluginManagement section and plugins is removed")
    public void thePluginConfigurationIsMovedToThePluginManagementSectionAndPluginsIsRemoved() {
        assertMigrationSuccess();
        assertTestFileMatchesExpectedFile("Failed to move the docker plugin and cleanup plugins section if it's the only one");
    }

    @Then("the plugin configuration is moved to a new pluginManagement section")
    public void thePluginConfigurationIsMovedToANewPluginManagementSection() {
        assertMigrationSuccess();
        assertTestFileMatchesExpectedFile("Failed to move the docker plugin if pluginManagement does not exist");
    }

    protected File getTestPom(String pomName) {
        return getTestFile(MIGRATION_DIR + packaging + File.separator + pomName);
    }
}
