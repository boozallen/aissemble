package com.boozallen.aiops.mda.generator;/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.metamodel.element.*;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class PropertiesStep extends AbstractModelInstanceSteps {
    private static final Logger logger = LoggerFactory.getLogger(PropertiesStep.class);

    @Before("@properties-generation")
    public void setup(Scenario scenario) throws IOException {
        this.scenario = scenario.getName();
    }

    @AfterStep("@properties-generation")
    public void cleanup(Scenario scenario) throws IOException {
        FileUtils.deleteDirectory(GENERATED_METADATA_DIRECTORY);
    }

    @Given("project called {string}")
    public void project_called(String projectName) throws IOException {
        createProject(projectName, "shared");
    }

    @Given("{string} pipeline is using {string}")
    public void pipeline_using(String typeName, String implName) throws IOException {
        createAndSavePipeline(unique("TestPipeline"), typeName, implName);
    }

    @When("the profile for {string} is generated")
    public void profile_spark_infra_is_generated(String profileName) throws Exception {
        readMetadata(projectName);
        Map<String, ExpandedProfile> profiles = loadProfiles();
        GenerateSourcesHelper.performSourceGeneration(profileName, profiles, this::createGenerationContext, (missingProfile, foundProfiles) -> {
            throw new RuntimeException("Missing profile: " + missingProfile);
        }, new Slf4jDelegate(logger), projectDir.toFile());
    }

    @Then("spark-infrastructure.properties file is generated in {string}")
    public void properties_file_generated(String propertiesPath) {
        Path properties = projectDir.resolve(propertiesPath);
        assertTrue("File not created: " + properties, Files.exists(properties) && Files.isRegularFile(properties));
    }

    @Then("spark-infrastructure.properties file generated in {string}, {string} properties are set to {string}")
    public void properties_set_values_correctly(String propertiesPath, String propertyName, String propertyValue) throws IOException {
        Path properties = projectDir.resolve(propertiesPath);
        List<String> lines = Files.readAllLines(properties);
        String expectedPropertiesMap = propertyName + "=" + propertyValue;
        assertTrue("Expected properties not found in " + propertiesPath, lines.contains(expectedPropertiesMap));
    }


}