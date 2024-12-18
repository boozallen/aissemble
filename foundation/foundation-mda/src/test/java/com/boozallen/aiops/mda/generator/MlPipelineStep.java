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
import com.boozallen.aiops.mda.metamodel.element.StepElement;
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
import org.technologybrewery.fermenter.mda.element.Target;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;
import org.technologybrewery.fermenter.mda.notification.Notification;
import org.technologybrewery.fermenter.mda.notification.NotificationCollector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MlPipelineStep extends AbstractModelInstanceSteps {
    private static final Logger logger = LoggerFactory.getLogger(MlPipelineStep.class);
    private static final List<String> STEPS = Arrays.asList("step-1", "step-2");
    private static final String PIPELINE_NAME = "example-pipelines-ml-pipeline-test";
    private static final String NEW_STEP = "inference-step-1";

    @Before("@ml-pipeline-generation")
    public void setup(Scenario scenario) throws IOException {
        this.scenario = scenario.getName();
        FileUtils.deleteDirectory(GENERATED_METADATA_DIRECTORY);
    }

    @Given("a machine-learning pipeline with multiple steps")
    public void a_machine_learning_pipeline_with_multiple_steps() throws IOException {
        createProject("example-pipelines", "ml-pipeline-test");
        createMlPipelineWithMultipleSteps();
    }

    @Given("a machine-learning pipeline")
    public void a_machine_learning_pipeline() throws IOException {
        a_machine_learning_pipeline_with_multiple_steps();
    }

    @Given("the pipeline step modules are already generated")
    public void the_pipeline_step_modules_are_already_generated() throws Exception {
        // completed
    }

    @Given("a new step is added to the meta model")
    public void a_new_step_is_added_to_the_meta_model() throws IOException {
        StepElement pipelineStep = createPipelineStep(NEW_STEP, "inference");
        pipeline.addStep(pipelineStep);
        savePipelineToFile(pipeline);
    }

    @Given("a machine-learning pipeline with missing step module in POM file")
    public void a_machine_learning_pipeline_with_missing_step_module() throws IOException {
        createProject("ml-pipeline-test", "step-1");
        createMlPipelineWithMultipleSteps();
    }

    @When("the aissemble-maven-modules profile is generated")
    public void the_aissemble_maven_modules_profile_is_generated() throws Exception {
        generateProfile("aissemble-maven-modules", this::createPipelinesRootPomContext);
    }

    @When("the machine-learning-pipeline profile is generated")
    public void the_machine_learning_pipeline_profile_is_generated() throws Exception {
        generateProfile("machine-learning-pipeline", this::createGenerationContext);
    }

    @Then("the pipeline POM is generated under the pipeline module")
    public void the_pipeline_pom_is_generated_under_the_pipeline_module() throws IOException {
        assertPomExists("the pipeline POM is generated under the pipeline module", PIPELINE_NAME);
    }

    @Then("a POM is generated for each step under the pipeline module")
    public void a_pom_is_generated_for_each_step_under_the_pipeline_module() throws IOException {
        for (String step: STEPS) {
            assertPomExists(String.format("%s module is generated under the pipeline module", step), PIPELINE_NAME, step);
        }
    }

    @Then("the step modules are listed in the pipeline POM")
    public void the_step_modules_are_listed_in_the_pipeline_pom() throws IOException {
        Path pom = projectDir.resolve(PIPELINE_NAME).resolve("pom.xml");
        assertModulesExistInPom(pom, STEPS, "The step modules are listed in the pipeline POM");
    }

    @Then("a POM is generated for new step under the pipeline module")
    public void a_pom_is_generated_for_new_step_under_the_pipeline_module() throws Exception {
        assertPomExists(String.format("%s module is generated under the pipeline module", NEW_STEP), PIPELINE_NAME, NEW_STEP);
    }

    @Then("a Manual Action is prompted for adding the step module to the pipeline")
    public void a_manual_action_is_prompted_for_adding_the_step_module_to_the_pipeline() throws IOException {
        Map<String, Map<String, Notification>> notifications = NotificationCollector.getNotifications();
        List<String> mlStepsInManualAction = new ArrayList<>();

        notifications.values().forEach(item -> {
            item.values().forEach(notification -> {
                for (String step: STEPS) {
                    if (notification.getItems().contains(String.format("<module>%s</module>", step))) {
                        mlStepsInManualAction.add(step);

                        if (mlStepsInManualAction.size() == STEPS.size()) {
                            break;
                        }
                    }
                }
            });
        });
        assertTrue("Missing steps are included in the Manual Action Prompt", mlStepsInManualAction.size() == STEPS.size());
    }

    private void createMlPipelineWithMultipleSteps() throws IOException{
        createAndSavePipeline("examplePipelinesMlPipelineTest",
                "machine-learning",
                "machine-learning-mlflow",
                pipeline -> {
            try {
                for (String step: STEPS) {
                    StepElement pipelineStep = createPipelineStep(step, "training");
                    pipeline.addStep(pipelineStep);
                }
            } catch (Exception e) {
                logger.error("Failed to add new step to test pipeline", e);
            }});
    }

    private void assertPomExists(String message, String... folders) {
        Path pom = projectDir;
        for (String folder :folders) {
            pom = pom.resolve(folder);
        }
        pom.resolve("pom.xml");
        assertNotNull(message, pom);
    }

    private void assertModulesExistInPom(Path pom, List<String> steps, String assertMessage) throws IOException {
        List<String> modules = new ArrayList<>();
        modules.addAll(steps);
        List<String> lines = Files.readAllLines(pom);
        for (String line: lines) {
            line = line.trim().replaceAll("<(\\/)?module>", "");
            if (modules.contains(line)){
                modules.remove(line);
                continue;
            }
            if (modules.isEmpty()){
                break;
            }
        }
        assertTrue(assertMessage, modules.isEmpty());
    }

    private void generateProfile(String profileName, Function<Target, GenerationContext> generateContext) throws Exception {
        readMetadata(projectName);
        Map<String, ExpandedProfile> profiles = loadProfiles();
        GenerateSourcesHelper.performSourceGeneration(profileName, profiles, generateContext, (missingProfile, foundProfiles) -> {
            throw new RuntimeException("Missing profile: " + missingProfile);
        }, new Slf4jDelegate(logger), projectDir.toFile());
    }

}
