package com.boozallen.aiops.mda.generator;/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */
import com.boozallen.aiops.mda.metamodel.element.AbstractModelInstanceSteps;

import org.apache.commons.io.FileUtils;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;

import org.technologybrewery.fermenter.mda.GenerateSourcesHelper;
import org.technologybrewery.fermenter.mda.element.ExpandedProfile;

import static org.junit.Assert.assertTrue;

public class DeploymentSteps extends AbstractModelInstanceSteps {

    private static final Logger logger = LoggerFactory.getLogger(DeploymentSteps.class);

    @Before("@deployment-generation")
    public void setup(Scenario scenario) throws IOException {
        this.scenario = scenario.getName();
        FileUtils.deleteDirectory(GENERATED_METADATA_DIRECTORY);

        // Create basic model and pipeline needed for generation
        // Implementation doesn't matter, just need a pipeline to run generation
        createProject("deployment-test", "deploy");
        createPipeline(unique("TestPipeline"), "data-flow", "data-delivery-spark");
    }

    @When("the deployment {string} is generated")
    public void the_deployment_generated(String profileName) throws Exception {
        Map<String, ExpandedProfile> profiles = loadProfiles();
        GenerateSourcesHelper.performSourceGeneration(profileName, profiles,
                this::createGenerationContext,
                (missingProfile, foundProfiles) -> {
                    throw new RuntimeException("Missing profile: " + missingProfile);
                },
                new Slf4jDelegate(logger),
                projectDir.toFile());
    }
}
