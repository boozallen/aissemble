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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class SparkApplicationStep extends AbstractModelInstanceSteps {
    private static final Logger logger = LoggerFactory.getLogger(SparkApplicationStep.class);

    @Before("@spark-application-generation")
    public void setup(Scenario scenario) throws IOException {
        this.scenario = scenario.getName();
        FileUtils.deleteDirectory(GENERATED_METADATA_DIRECTORY);
    }

    @Given("a spark project named {string}")
    public void a_spark_project_named(String projectName) throws IOException {
        createProject(projectName, "shared");
    }

    @Given("a data-flow pipeline using data-delivery-spark")
    public void a_dataflow_pipeline_using_data_delivery_spark() throws IOException {
        a_pipeline_using_data_delivery("data-flow", "data-delivery-spark");
    }

    @Given("a dataflow pipeline using {string}")
    public void a_pipeline_using_data_delivery(String typeName, String implName) throws IOException {
        createAndSavePipeline("SparkPipeline", "data-flow", implName);
    }

    @Given("a file store named {string}")
    public void a_file_store_named(String fileStoreName) throws IOException {
        List<String> fileStoreNames = new ArrayList<>();
        fileStoreNames.add(fileStoreName);
        saveFileStores(createFileStores(fileStoreNames));
    }

    @Given("two file stores named {string} and {string}")
    public void two_file_stores_named_and(String fileStoreOneName, String fileStoreTwoName) throws IOException {
        List<String> fileStoreNames = new ArrayList<>();
        fileStoreNames.add(fileStoreOneName);
        fileStoreNames.add(fileStoreTwoName);
        saveFileStores(createFileStores(fileStoreNames));
    }

    @When("the profile data-delivery-spark-pipeline is generated")
    public void the_profile_data_delivery_spark_pipeline_is_generated() throws Exception {
        readMetadata(projectName);
        Map<String, ExpandedProfile> profiles = loadProfiles();
        GenerateSourcesHelper.performSourceGeneration("data-delivery-spark-pipeline", profiles, this::createGenerationContext, (missingProfile, foundProfiles) -> {
            throw new RuntimeException("Missing profile: " + missingProfile);
        }, new Slf4jDelegate(logger), projectDir.toFile());
    }

    @Then("the {string}, {string}, and {string} configurations {string} generated")
    public void the_and_configurations_are_not_generated(String fsProvider, String fsAccessKeyId, String fsSecretAccessKey, String expectation) throws IOException {
        AtomicBoolean hasFsProviderEnvVar = new AtomicBoolean(false);
        AtomicBoolean hasFsAccessKeyIdEnvVar = new AtomicBoolean(false);
        AtomicBoolean hasFsSecretAccessKeyEnvVar = new AtomicBoolean(false);

        String pipelineSparkBaseValuesYamlFileName = "spark-pipeline-base-values.yaml";
        File pipelineSparkBaseValuesYamlFile = new File(projectDir.toString() + "/main/resources/apps/" + pipelineSparkBaseValuesYamlFileName);

        assertTrue("The " + pipelineSparkBaseValuesYamlFileName + " file was not generated!", pipelineSparkBaseValuesYamlFile.exists());

        Files.lines(Paths.get(pipelineSparkBaseValuesYamlFile.getAbsolutePath())).forEach(line -> {
            if(line.contains("- name: \"" + fsProvider + "\"")) {
                hasFsProviderEnvVar.set(true);
            }

            if(line.contains("- name: \"" + fsAccessKeyId + "\"")) {
                hasFsAccessKeyIdEnvVar.set(true);
            }

            if(line.contains("- name: \"" + fsSecretAccessKey + "\"")) {
                hasFsSecretAccessKeyEnvVar.set(true);
            }
        });

        assertEquals("File Store Env variable \"" + fsProvider + "\" found in " + pipelineSparkBaseValuesYamlFile, hasFsProviderEnvVar.get(), expectation.equals("are"));
        assertEquals("File Store Env variable \"" + fsAccessKeyId + "\" found in " + pipelineSparkBaseValuesYamlFile, hasFsAccessKeyIdEnvVar.get(), expectation.equals("are"));
        assertEquals("File Store Env variable \"" + fsAccessKeyId + "\" found in " + pipelineSparkBaseValuesYamlFile, hasFsSecretAccessKeyEnvVar.get(), expectation.equals("are"));
    }

    /**
     * Create list of file store based on a list of file store names
     *
     * @param fileStoreNames the List of File Store Names
     */
    private List<FileStore> createFileStores(List<String> fileStoreNames) {
        List<FileStore> fileStores = new ArrayList<FileStore>();
        for (String fileStoreName : fileStoreNames) {
            FileStoreElement fileStoreElement = new FileStoreElement(fileStoreName);
            fileStores.add(fileStoreElement);
        }
        return fileStores;
    }

    protected void saveFileStores(List<FileStore> fileStores) throws IOException  {
        this.pipeline.setFileStores(fileStores);
        savePipelineToFile(pipeline);
    }
}
