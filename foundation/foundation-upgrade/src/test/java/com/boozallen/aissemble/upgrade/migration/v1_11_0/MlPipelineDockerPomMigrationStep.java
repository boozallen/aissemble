package com.boozallen.aissemble.upgrade.migration.v1_11_0;

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
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.technologybrewery.baton.util.pom.PomHelper;

import static org.junit.Assert.assertTrue;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MlPipelineDockerPomMigrationStep extends AbstractMigrationTest {
    public static final String MIGRATION_ROOT = "v1_11_0/MlPipelineDockerPomMigration/migration/%s/%s-pom.xml";

    @Given("a {string} docker POM with no dependencies defined")
    public void aMlStepDockerPomWithNoDependenciesDefined(String step) {
        testFile = getTestFile(String.format(MIGRATION_ROOT, "no-dep", step));
    }

    @Given("a {string} docker POM with one dependencies defined")
    public void aMlStepDockerPomWithOneDependencyDefined(String step) {
        testFile = getTestFile(String.format(MIGRATION_ROOT, "one-dep", step));
    }

    @Given("the existing dependency artifactId is not {string}")
    public void theDependencyArtifactIsNot(String step) {
        Model model = PomHelper.getLocationAnnotatedModel(testFile);
        assertTrue("The existing dependency artifactId is not ml step.",!model.getDependencies().get(0).getArtifactId().equals(step));
    }

    @Given("a {string} docker POM with the same step dependency defined")
    public void aMlStepDockerPomWithStepDependencyDefined(String step) {
        testFile = getTestFile(String.format(MIGRATION_ROOT, "skip", step));
        Model model = PomHelper.getLocationAnnotatedModel(testFile);
        assertTrue("The dependency artifactId is ml step.",model.getDependencies().get(0).getArtifactId().equals(step));
    }

    @When("the 1.11.0 ml pipeline docker pom migration executes")
    public void mlPipelineDockerPomMigrationExecutes() {
        MlPipelineDockerPomMigrationTest migration = new MlPipelineDockerPomMigrationTest();
        performMigration(migration);
    }

    @Then("the {string} dependency is add")
    public void theMlStepDependencyIsAdded(String step) {
        String testFilePath = testFile.getPath().replaceAll("migration", "validation");
        File validatedFile = new File(testFilePath);
        assertLinesMatch(String.format("The $s dependency is added to docker POM.", step), validatedFile, testFile);
    }

    @Then("the ml migration is skipped")
    public void theMlMigrationIsSkipped() {
        assertMigrationSkipped();
    }

    public class MlPipelineDockerPomMigrationTest extends MlPipelineDockerPomMigration {
        protected MavenProject getRootProject() {
            // root project
            MavenProject project = new MavenProject();
            project.setArtifactId("pipelines");

            // set ml project
            MavenProject mlProject = new MavenProject();
            mlProject.setArtifactId("ml-pipeline-test");
            Model model = new Model();

            Plugin plugin = new Plugin();
            plugin.setArtifactId("fermenter-mda");
            plugin.setGroupId("org.technologybrewery.fermenter");

            // set plugin profile configuration
            Xpp3Dom profile = new Xpp3Dom("profile");
            profile.setValue("machine-learning-pipeline");

            Xpp3Dom configuration = new Xpp3Dom("configuration");
            configuration.addChild(profile);
            plugin.setConfiguration(configuration);

            Build build = new Build();
            build.setPlugins(Arrays.asList(plugin));

            List<String> modules = Arrays.asList("<module>training-step</module>", "<module>inference-step</module>");

            model.setBuild(build);
            model.setModules(modules);

            mlProject.setModel(model);
            
            project.setCollectedProjects(Arrays.asList(mlProject));
            return project;
        }
    }

}
