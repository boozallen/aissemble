package com.boozallen.aiops.mda.generator;

/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.metamodel.element.AbstractModelInstanceSteps;
import com.boozallen.aiops.mda.metamodel.element.ModelLineageElement;
import com.boozallen.aiops.mda.metamodel.element.StepElement;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
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

public class DockerSteps extends AbstractModelInstanceSteps {

    private static final Logger logger = LoggerFactory.getLogger(AbstractModelInstanceSteps.class);

    @Given("a project named {string} with a source code url of {string}")
    public void a_project_named_with_a_source_code_url_of(String projectName, String url) throws IOException {
        createProject(projectName, "docker", url);
    }

    @Given("a data-flow pipeline using {string} with data-lineage enabled")
    public void a_data_flow_pipeline_using_with_data_lineage_enabled(String implName) throws IOException {
        createPipeline(unique("DockerPipeline"), "data-flow", implName, p -> p.setDataLineage(true));
    }

    @Given("a machine-learning pipeline using {string} with model-lineage enabled")
    public void a_machine_learning_pipeline_using_with_model_lineage_enabled(String implName) throws IOException {
        createPipeline(unique("DockerPipeline"), "machine-learning", implName, pipeline -> {
            StepElement step = createTrainingStepWithLineage("test-training-step");
            pipeline.addStep(step);
        });
    }

    @When("the docker profile {string} is generated")
    public void the_docker_profile_is_generated(String profileName) throws Exception {
        readMetadata(projectName);
        Map<String, ExpandedProfile> profiles = loadProfiles();
        GenerateSourcesHelper.performSourceGeneration(profileName, profiles,
                this::createGenerationContext,
                (missingProfile, foundProfiles) -> {
                    throw new RuntimeException("Missing profile: " + missingProfile);
                },
                new Slf4jDelegate(logger),
                projectDir.toFile());
    }

    @Then("my lineage property {string} is set to {string}")
    public void my_lineage_property_is_set_to(String producerKey, String producerValue) throws IOException {
        File propertiesFile = new File(projectDir.toString() + "/main/resources/krausening/base/data-lineage.properties");
        assertTrue("Lineage properties file for " + projectDir.toString() + " was not generated!", propertiesFile.exists());
        AtomicBoolean configFound = new AtomicBoolean(false);
        Files.lines(Paths.get(propertiesFile.getAbsolutePath())).forEach(line -> {
            if(line.contains(producerKey + "=" + producerValue)) {
                configFound.set(true);
            }
        });
        assertTrue("Default producer config was not generated automatically", configFound.get());
    }

    private static StepElement createTrainingStepWithLineage(String name) {
        ModelLineageElement modelLineage = new ModelLineageElement();
        modelLineage.setEnabled(true);
        StepElement step = new StepElement();
        step.setName(name);
        step.setType("training");
        step.setModelLineage(modelLineage);
        return step;
    }
}
