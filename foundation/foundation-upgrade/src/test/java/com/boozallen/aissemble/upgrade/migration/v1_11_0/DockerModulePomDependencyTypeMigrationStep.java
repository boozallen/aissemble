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
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;

public class DockerModulePomDependencyTypeMigrationStep extends AbstractMigrationTest {
    private static final Logger logger = LoggerFactory.getLogger(DockerModulePomDependencyTypeMigrationStep.class);
    public static final String MIGRATION_ROOT = "v1_11_0/DockerModulePomDependencyTypeMigration/migration/%s/pom.xml";

    @Given("a docker module pom that has the {string} dependency with pom type")
    public void aDockerModulePomThatHasTheDependencyWithPomType(String artifact) {
        testFile = getTestFile(String.format(MIGRATION_ROOT, artifact));
    }

    @Given("a docker module pom that has the {string} dependency with {string} type")
    public void aDockerModulePomThatHasTheDependencyAndType(String artifact, String type) {
        testFile = getTestFile(String.format(MIGRATION_ROOT, artifact));
    }

    @When("the docker module pom dependency type migration executes")
    public void PipelineInvocationServiceMigrationExecutes() {
        DockerModulePomDependencyTypeMigrationTest migration = new DockerModulePomDependencyTypeMigrationTest();
        performMigration(migration);
    }

    @Then("the docker module pom dependency type is changed to {string}")
    public void theDockerModulePomDependencyTypeIsChangedTo(String type) {
        String file = testFile.getParent().replaceAll("^.+/migration", "") + "/" + testFile.getName();
        File validatedFile = getTestFile("/v1_11_0/DockerModulePomDependencyTypeMigration/validation/" + file);
        assertLinesMatch(String.format("The docker module pom dependency type is changed to %s.", type), validatedFile, testFile);
    }

    @Then("the docker module pom dependency type migration is skipped")
    public void theDockerModulePomDependencyTypeMigrationIsSkipped() {
        assertMigrationSkipped();
    }


    public class DockerModulePomDependencyTypeMigrationTest extends DockerModulePomDependencyTypeMigration {
        protected MavenProject getRootProject() {
            MavenProject project = new MavenProject();
            project.setArtifactId("test-pipelines");
            MavenProject pysparkPipeline = new MavenProject();
            pysparkPipeline.setArtifactId("pyspark-pipeline");
            pysparkPipeline.setPackaging("habushu");
            pysparkPipeline.setParent(project);
            MavenProject sparkPipeline = new MavenProject();
            sparkPipeline.setArtifactId("spark-pipeline");
            sparkPipeline.setPackaging("jar");
            sparkPipeline.setParent(project);
            project.setCollectedProjects(Arrays.asList(sparkPipeline, pysparkPipeline));
            return project;
        }
    }
}
