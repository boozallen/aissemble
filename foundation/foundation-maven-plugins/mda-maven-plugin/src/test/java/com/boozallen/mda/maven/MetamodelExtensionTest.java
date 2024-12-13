package com.boozallen.mda.maven;

import static org.junit.Assert.assertNull;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Maven Plugins::MDA Maven Plugin
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

import com.boozallen.mda.maven.mojo.PipelineArtifactsMojo;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class MetamodelExtensionTest {
    private MojoTestCaseWrapper mojoTestCase = new MojoTestCaseWrapper();
    private Exception exception;
    private File modelSource;
    private String modelRepositoryImpl;
    private String testProject;

    @Before("@MetamodelExtension")
    public void setup() throws Exception {
        mojoTestCase.configurePluginTestHarness();
        Path testPom = Paths.get("src", "test", "resources", "test-pom", "pom.xml").toAbsolutePath();
        Path testProject = Paths.get("target", "test-project").toAbsolutePath();
        Files.createDirectories(testProject);
        Files.copy(testPom, testProject.resolve("pom.xml"));
        this.testProject = testProject.toString();
    }

    @After("@MetamodelExtension")
    public void teardown() throws Exception {
        mojoTestCase.tearDownPluginTestHarness();
        FileUtils.deleteDirectory(Paths.get(this.testProject).toFile());
    }

    @Given("a model instance repository extending foundation-mda")
    public void a_model_instance_repository_extending_foundation_mda() {
        this.modelRepositoryImpl = "com.boozallen.mda.maven.metadata.repository.ExtensionModelInstanceRepository";
    }

    @Given("a pipeline with metamodel extensions")
    public void a_pipeline_with_metamodel_extensions() {
        this.modelSource = new File("src/test/resources/models/model-extension.jar");
    }

    @When("the copy-pipeline-artifacts goal is executed")
    public void the_copy_pipeline_artifacts_goal_is_executed() {
        //Read in the test pom  for the correct pipeline type and configure the mojo with the parameters
        File testPom = new File(testProject + "/pom.xml");

        try {
            PipelineArtifactsMojo mojo = (PipelineArtifactsMojo) mojoTestCase.lookupConfiguredMojo(testPom, "copy-pipeline-artifacts");
            mojo.setModelsSource(this.modelSource);
            mojo.setPipelinesDirectory(new File("src/test/resources/pipelines/data-flow").getAbsolutePath() + "/");
            mojo.setMetadataRepositoryImpl(this.modelRepositoryImpl);
            mojo.execute();
        } catch (Exception exception) {
            this.exception = exception;
        }
    }

    @Then("the extended metamodel is read successfully")
    public void the_extended_metamodel_is_read_successfully() {
        assertNull("An exception occurred when executing the mojo with a metamodel repository extension", this.exception);
    }
}
