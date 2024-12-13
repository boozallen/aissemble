package com.boozallen.aiops.mda.metamodel.element;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.generator.GenerationException;
import org.technologybrewery.fermenter.mda.util.JsonUtils;

import com.boozallen.aiops.mda.generator.post.action.PostActionType;
import com.boozallen.aiops.mda.util.TestMetamodelUtil;
import com.boozallen.aiops.mda.metamodel.json.AissembleMdaJsonUtils;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class PostActionSteps extends AbstractModelInstanceSteps {

    private static final String TEST_PACKAGE = "post.action.steps";
    private static final String TEST_PIPELINE_TYPE = "test-type";
    private static final String TEST_PIPELINE_IMPL = "test-implementation";

    private File pipelineFile;
    private GenerationException encounteredException;

    @Before("@postAction")
    public void setUp() {
        AissembleMdaJsonUtils.configureCustomObjectMappper();
    }

    @After("@postAction")
    public void cleanup() {
        PostActionElement.messageTracker.clear();
        pipelineFile = null;
        encounteredException = null;
    }

    @Given("an otherwise valid pipeline with a step containing a post-action with name {string} and type {string}")
    public void an_otherwise_valid_pipeline_with_a_step_containing_a_post_action_with_name_and_type(String name,
            String type) throws Exception {
        PostActionElement postAction = new PostActionElement();
        if (StringUtils.isNotBlank(name)) {
            postAction.setName(name);
        }
        if (StringUtils.isNotBlank(type)) {
            postAction.setType(type);
        }

        pipelineFile = createPipelineWithPostAction("post-action-validation", postAction);
    }

    @Given("an otherwise valid pipeline with a step containing a model-conversion post-action with model source {string} and model target {string}")
    public void an_otherwise_valid_pipeline_with_a_step_containing_a_model_conversion_post_action_with_model_source_and_model_target(
            String modelSource, String modelTarget) throws Exception {
        PostActionElement postAction = new PostActionElement();
        postAction.setName(RandomStringUtils.insecure().nextAlphabetic(8));
        postAction.setType(PostActionType.MODEL_CONVERSION.getValue());
        if (StringUtils.isNotBlank(modelSource)) {
            postAction.setModelSource(modelSource);
        }
        if (StringUtils.isNotBlank(modelTarget)) {
            postAction.setModelTarget(modelTarget);
        }

        pipelineFile = createPipelineWithPostAction("model-conversion-post-action-validation", postAction);
    }

    @When("the pipeline with post-actions is read")
    public void the_pipeline_with_post_actions_is_read() {
        encounteredException = null;

        try {
            PipelineElement pipeline = JsonUtils.readAndValidateJson(pipelineFile, PipelineElement.class);
            assertNotNull("Could not read pipeline file!", pipeline);

        } catch (GenerationException e) {
            encounteredException = e;
        }
    }

    @Then("the generator throws an exception due to invalid post-action metamodel information")
    public void the_generator_throws_an_exception_due_to_invalid_post_action_metamodel_information() {
        assertNotNull("A GenerationException should have been thrown!", encounteredException);
    }

    @Then("an error is thrown due to missing post-action metamodel information")
    public void an_error_is_thrown_due_to_missing_post_action_metamodel_information() {
        assertTrue("No error thrown for missing post-action metamodel information",
                PostActionElement.messageTracker.hasErrors());
    }

    private File createPipelineWithPostAction(String pipelineName, PostAction postAction) throws Exception {
        PipelineElement pipeline = TestMetamodelUtil.createPipelineWithType(pipelineName, TEST_PACKAGE,
                TEST_PIPELINE_TYPE, TEST_PIPELINE_IMPL);

        StepElement step = TestMetamodelUtil.getRandomStep();
        step.addPostAction(postAction);

        pipeline.addStep(step);

        pipeline.validate();

        return savePipelineToFile(pipeline);
    }

}
