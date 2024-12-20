package com.boozallen.aiops.mda.generator;/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.generator.util.PipelineUtils;

import com.boozallen.aiops.mda.metamodel.element.*;
import com.boozallen.aiops.mda.metamodel.element.StepElement;
import com.boozallen.aiops.mda.metamodel.element.RecordElement;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.fermenter.mda.GenerateSourcesHelper;
import org.technologybrewery.fermenter.mda.element.ExpandedProfile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertTrue;

public class PyprojectGenerationSteps extends AbstractModelInstanceSteps {
    private static final Logger logger = LoggerFactory.getLogger(PyprojectGenerationSteps.class);
    private RecordElement record;

    @Before("@pyproject-generation")
    public void setup(Scenario scenario) throws IOException {
        this.scenario = scenario.getName();
        FileUtils.deleteDirectory(GENERATED_METADATA_DIRECTORY);
    }

    @Given("a project with the name {string}")
    public void a_project_with_the_name(String projectName) throws IOException {
        createProject(projectName, "shared");
    }

    @Given("a {string} pipeline using the {string} implementation")
    public void a_pipeline_using_the_implementation(String pipelineType, String pipelineImplementation) throws IOException {
        String pipelineName = pipelineType + "-pipeline";
        createAndSavePipeline(pipelineName, pipelineType, pipelineImplementation);
    }

    @Given("a {string} pipeline with step type {string} using the {string} implementation")
    public void a_pipeline_with_step_type_using_the_implementation(String pipelineType, String pipelineStepType, String pipelineImplementation) throws IOException {
        String pipelineName = pipelineType + "-pipeline";
        String pipelineStepName = pipelineStepType + "-step";
        
        createAndSavePipeline(pipelineName, pipelineType, pipelineImplementation, pipeline -> {
            try {
                StepElement pipelineStep = createPipelineStep(pipelineStepName, pipelineStepType);
                pipeline.addStep(pipelineStep);
            } catch (Exception e) {
                logger.error("Failed to add new step to test pipeline", e);
            };

            });
    }

    @Given("a record model with a corresponding dictionary type defined")
    public void a_record_model_with_a_corresponding_dictionary_type_defined() throws Exception {
        createSampleDictionaryType();
        record = new RecordElement();
        record.setName("TestRecord");
        record.setPackage(BOOZ_ALLEN_PACKAGE);
        saveRecordToFile(record);
    }

    @When("the {string} profile {string} is generated")
    public void the_profile_is_generated(String profileType, String profile) throws Exception {
        readMetadata(projectName);
        Map<String, ExpandedProfile> profiles = loadProfiles();
        GenerateSourcesHelper.performSourceGeneration(profile, profiles, this::createGenerationContext, (missingProfile, foundProfiles) -> {
            throw new RuntimeException("Missing profile: " + missingProfile);
        }, new Slf4jDelegate(logger), projectDir.toFile());
    }

    @Then("the {string} is generated with the minimum Python version, {string}")
    public void the_is_generated_with_the_minimum_Python_version(String outputFile, String minPythonVersion) throws IOException {
        File fullFilepath = new File(projectDir.toString(), outputFile);

        assertTrue("The " + outputFile + " file was not generated!", fullFilepath.exists());

        AtomicBoolean minPythonVersionFound = fileInspector(fullFilepath, "python = \"" + minPythonVersion+ "\"");

        assertTrue("The minimum Python version " + minPythonVersion + " was NOT found in " + fullFilepath + ".", minPythonVersionFound.get());
    }

    private AtomicBoolean fileInspector(File filepath, String patternToFind) throws IOException {
        AtomicBoolean patternFound = new AtomicBoolean(false);
        Files.lines(Paths.get(filepath.getAbsolutePath())).forEach(line -> {
            if(line.contains(patternToFind)) {
                patternFound.set(true);
            }
        });
        return patternFound;    
    }
}
