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

import com.boozallen.aiops.mda.generator.util.PipelineUtils;
import com.boozallen.aiops.mda.util.TestMetamodelUtil;
import com.boozallen.aiops.mda.metamodel.json.AiopsMdaJsonUtils;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.generator.GenerationException;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class NameTranslationSteps {

	private static final String BOOZ_ALLEN_PACKAGE = "com.boozallen.aiops";

	protected Pipeline pipeline;
	protected GenerationException encounteredException;
	protected StepElement testStep;
	private PipelineElement newPipeline;
	private String translatedPipelineName;
	private String translatedStepName;

	@Before("@nametranslation")
	public void setUp() {
		AiopsMdaJsonUtils.configureCustomObjectMappper();
		testStep = new StepElement();
	}

	@After("@nametranslation")
	public void cleanup() {
		StepDataBindingElement.messageTracker.clear();
	}

	@Given("a pipeline named {string}")
	public void a_pipeline_named(String pipelineName) throws Exception {
		newPipeline = TestMetamodelUtil.createPipelineWithType(pipelineName, BOOZ_ALLEN_PACKAGE, "data-flow", "data-delivery-pyspark");
	}
	@When("the names are translated to Python package format")
	public void the_names_are_translated_to_python_package_format() {
		translatedPipelineName = PipelineUtils.deriveLowercaseSnakeCaseNameFromCamelCase(newPipeline.getName());
	}
	@Then("the translated pipeline name is {string}")
	public void the_translated_pipeline_name_is(String outputPipelineName) {
		if (newPipeline == null) {
			throw new RuntimeException("Problem reading pipeline:", encounteredException);
		}
		assertEquals("The pipeline name was not properly converted to the python package name format", outputPipelineName, translatedPipelineName);
	}

	@Given("a pipeline named {string} and a step named {string}")
	public void a_pipeline_named_and_a_step_named(String pipelineName, String stepName) throws Exception {
		newPipeline = TestMetamodelUtil.createPipelineWithType(pipelineName, BOOZ_ALLEN_PACKAGE, "data-flow", "data-delivery-pyspark");

		StepElement step = new StepElement();
		if (StringUtils.isNotBlank(stepName)) {
			step.setName(stepName);
		}
		String stepType = "synchronous";
		if (StringUtils.isNotBlank(stepType)) {
			step.setType(stepType);
		}
		newPipeline.addStep(step);
	}
	@When("the names are translated to Python format")
	public void the_names_are_translated_to_python_format() {
		if(newPipeline.getSteps().get(0) != null) {
			translatedStepName = newPipeline.getSteps().get(0).getName();
		}
	}

	@Then("the translated pipeline class name is {string}")
	public void the_translated_pipeline_class_name_is(String outputPipelineName) {
		assertEquals("The pipeline name was not properly converted to the python or java class name format", outputPipelineName, newPipeline.getName());
	}

	@Then("the translated step class name is {string}")
	public void the_translated_step_class_name_is(String outputStepName) {
		assertEquals("The step name was not properly converted to the python or java step class name format", outputStepName, translatedStepName);
	}

	@Given("a spark pipeline named {string} and a step named {string}")
	public void a_spark_pipeline_named_and_a_step_named(String pipelineName, String stepName) throws Exception {
		newPipeline = TestMetamodelUtil.createPipelineWithType(pipelineName, BOOZ_ALLEN_PACKAGE, "data-flow", "data-delivery-spark");

		StepElement step = new StepElement();
		if (StringUtils.isNotBlank(stepName)) {
			step.setName(stepName);
		}
		String stepType = "synchronous";
		if (StringUtils.isNotBlank(stepType)) {
			step.setType(stepType);
		}
		newPipeline.addStep(step);
	}

	@When("the names are translated to Java class format")
	public void the_names_are_translated_to_java_class_format() {
		if(newPipeline.getSteps().get(0) != null) {
			translatedStepName = newPipeline.getSteps().get(0).getName();
		}
	}

	@When("the names are translated to Maven artifact ID format")
	public void the_names_are_translated_to_maven_artifact_id_format() {
		translatedPipelineName = PipelineUtils.deriveArtifactIdFromCamelCase(newPipeline.getName());
		if(newPipeline.getSteps().get(0) != null) {
			translatedStepName = PipelineUtils.deriveArtifactIdFromCamelCase(newPipeline.getSteps().get(0).getName());
		}
	}

	@Then("the translated maven pipeline name is {string}")
	public void the_translated_maven_pipeline_name_is(String outputPipelineName) {
		assertEquals("The pipeline name was not properly converted to the maven pipeline name format", outputPipelineName, translatedPipelineName);
	}
	@Then("the maven translated step name is {string}")
	public void the_maven_translated_step_name_is(String outputStepName) {
		assertEquals("The step name was not properly converted to the maven step name format", outputStepName, translatedStepName);
	}

}
