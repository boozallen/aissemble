package com.boozallen.mda.maven;

import static org.junit.Assert.assertNull;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Maven Plugins::MDA Maven Plugin
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
