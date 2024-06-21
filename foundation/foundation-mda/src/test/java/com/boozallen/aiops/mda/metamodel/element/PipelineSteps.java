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

import com.boozallen.aiops.mda.generator.common.DataFlowStrategy;
import com.boozallen.aiops.mda.generator.common.MachineLearningStrategy;
import com.boozallen.aiops.mda.generator.common.PipelineContext;
import com.boozallen.aiops.mda.util.TestMetamodelUtil;
import com.boozallen.aiops.mda.metamodel.json.AiopsMdaJsonUtils;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.technologybrewery.fermenter.mda.generator.GenerationException;
import org.technologybrewery.fermenter.mda.metamodel.element.ConfigurationItemElement;
import org.technologybrewery.fermenter.mda.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.fermenter.mda.util.MessageTracker;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PipelineSteps extends AbstractModelInstanceSteps {

    private static final Logger logger = LoggerFactory.getLogger(PipelineSteps.class);
    private static final String BOOZ_ALLEN_PACKAGE = "com.boozallen.aiops";
    public static final String ABSTRACT_DATA_ACTION_IMPL_PY = "test/step/abstract_data_action_impl.py";
    public static final String ABSTRACT_PIPELINE_STEP_PY = "generated-test/step/abstract_pipeline_step.py";
    public static final String DO_MODIFY_REGEX = "DO\\s+MODIFY";
    public static final String ABSTRACT_DATA_ACTION_IMPL_INHERIT_REGEX = "AbstractDataActionImpl\\(AbstractDataAction\\):";
    public static final String ABSTRACT_PIPELINE_STEP_INHERIT_REGEX = "AbstractPipelineStep\\(AbstractDataActionImpl\\)";

    protected Pipeline pipeline;
    protected Pipeline dataFLowPipeline;
    protected Pipeline machineLearningPipeline;
    protected GenerationException encounteredException;
    protected StepElement testStep;

    private List<Platform> testPlatforms;
    private List<String> executionHelpers;

    @Before("@pipeline")
    public void setUp() {
        AiopsMdaJsonUtils.configureCustomObjectMappper();
        testStep = new StepElement();
    }

    @After("@pipeline")
    public void cleanup() {
        StepDataBindingElement.messageTracker.clear();
        testPlatforms = null;
    }

    @Given("a pipeline described by {string}, {string}, {string}, {string}")
    public void a_pipeline_described_by(String name, String packageName, String typeName, String implementation)
            throws Exception {
        PipelineElement newPipeline = TestMetamodelUtil.createPipelineWithType(name, packageName, typeName, implementation);
        pipelineFile = savePipelineToFile(newPipeline);

    }

    @Given("a pipeline with {int} with the name {string} and {string}")
    public void a_pipeline_with_with_the_name_and(Integer numberOfSteps, String name, String packageName)
            throws Exception {
        PipelineElement newPipeline = TestMetamodelUtil.createPipelineWithType(name, packageName, "data-flow", "test-implementation");

        List<Step> steps = createStepsForDataFlowPipeline(numberOfSteps);

        steps.forEach((step) -> newPipeline.addStep(step));

        pipelineFile = savePipelineToFile(newPipeline);
    }

    @Given("an otherwise valid pipeline with a step name {string} and type {string}")
    public void an_otherwise_valid_pipeline_with_a_step_name_and_type(String stepName, String stepType) throws Exception {
        String pipelineName = "step-validation";
        PipelineElement newPipeline = TestMetamodelUtil.createPipelineWithType(pipelineName, BOOZ_ALLEN_PACKAGE, "data-flow", "test-implementation");

        StepElement step = new StepElement();
        if (StringUtils.isNotBlank(stepName)) {
            step.setName(stepName);
        }

        if (StringUtils.isNotBlank(stepType)) {
            step.setType(stepType);
        }

        newPipeline.addStep(step);

        pipelineFile = savePipelineToFile(newPipeline);
    }

    @Given("an otherwise valid pipeline with a step containing an unspecified persist type")
    public void an_otherwise_valid_pipeline_with_an_unspecified_persist_type() throws Exception {
        String pipelineName = "persist-validation";
        PipelineElement newPipeline = TestMetamodelUtil.createPipelineWithType(pipelineName, BOOZ_ALLEN_PACKAGE, "data-flow", "test-implementation");

        StepElement step = TestMetamodelUtil.getRandomStep();

        PersistElement persist = new PersistElement();
        step.setPersist(persist);

        newPipeline.addStep(step);

        pipelineFile = savePipelineToFile(newPipeline);
    }

    @Given("a valid pipeline with a step containing a specified persist mode")
    public void a_valid_pipeline_with_a_step_containing_a_specified_persist_save_mode() throws Exception {
        String pipelineName = "persist-mode-validation";
        PipelineElement newPipeline = TestMetamodelUtil.createPipelineWithType(pipelineName, BOOZ_ALLEN_PACKAGE,
                "data-flow", "test-implementation");

        StepElement step = TestMetamodelUtil.getRandomStep();

        PersistElement persist = new PersistElement();
        persist.setType("test-persist-type");
        persist.setMode("test-mode");
        step.setPersist(persist);

        newPipeline.addStep(step);

        pipelineFile = savePipelineToFile(newPipeline);
    }

    @Given("an otherwise valid pipeline with a step containing an unspecified inbound type")
    public void an_otherwise_valid_pipeline_with_a_step_containing_an_unspecified_inbound_type() throws Exception {
        String pipelineName = "inbound-validation";
        PipelineElement newPipeline = TestMetamodelUtil.createPipelineWithType(pipelineName, BOOZ_ALLEN_PACKAGE, "data-flow", "test-implementation");

        StepElement step = TestMetamodelUtil.getRandomStep();

        StepDataBindingElement inbound = new StepDataBindingElement();
        step.setInbound(inbound);

        newPipeline.addStep(step);

        pipelineFile = savePipelineToFile(newPipeline);
    }

    @Given("an otherwise valid pipelinewith a step containing an unspecified outbound type")
    public void an_otherwise_valid_pipeline_with_an_unspecified_outbound_type() throws Exception {
        String pipelineName = "outbound-validation";
        PipelineElement newPipeline = TestMetamodelUtil.createPipelineWithType(pipelineName, BOOZ_ALLEN_PACKAGE, "data-flow", "test-implementation");

        StepElement step = TestMetamodelUtil.getRandomStep();

        StepDataBindingElement outbound = new StepDataBindingElement();
        step.setOutbound(outbound);

        newPipeline.addStep(step);

        pipelineFile = savePipelineToFile(newPipeline);
    }

    @Given("an otherwise valid pipeline with a step containing a configuration item with key {string} and value {string}")
    public void an_otherwise_valid_pipeline_with_a_step_containing_a_configuration_item_with_key_and_value(String key, String value) throws Exception {
        String pipelineName = "configuration-item-validation";
        PipelineElement newPipeline = TestMetamodelUtil.createPipelineWithType(pipelineName, BOOZ_ALLEN_PACKAGE, "data-flow", "test-implementation");

        StepElement step = TestMetamodelUtil.getRandomStep();

        ConfigurationItemElement configurationItem = new ConfigurationItemElement();
        if (StringUtils.isNotBlank(key)) {
            configurationItem.setKey(key);
        }

        if (StringUtils.isNotBlank(value)) {
            configurationItem.setValue(value);
        }

        step.addConfigurationItem(configurationItem);

        newPipeline.addStep(step);

        pipelineFile = savePipelineToFile(newPipeline);
    }

    @Given("a valid pipeline with provenance configuration")
    public void aValidPipelineWithProvenanceConfiguration() throws IOException {
        final String name = "explicit-provenance";
        final PipelineElement newPipeline =
                TestMetamodelUtil.createPipelineWithType(name, BOOZ_ALLEN_PACKAGE,
                        "data-flow", "default");

        final StepElement step = new StepElement();
        step.setName("step-0");
        step.setType("ingest");

        final ProvenanceElement provenanceElement = new ProvenanceElement();
        step.setProvenance(provenanceElement);

        final StepDataBindingElement inbound = new StepDataBindingElement();
        inbound.setType("messaging");
        inbound.setChannelName("unit-test-inbound");
        inbound.setChannelType("topic");
        step.setInbound(inbound);

        final StepDataBindingElement outbound = new StepDataBindingElement();
        outbound.setType("messaging");
        outbound.setChannelName("unit-test-outbound");
        outbound.setChannelType("queue");
        step.setOutbound(outbound);

        newPipeline.addStep(step);

        pipelineFile = savePipelineToFile(newPipeline);
    }

    @Given("a valid pipeline with provenance disabled")
    public void aValidPipelineWitProvenanceDisabled() throws IOException {
        final String name = "explicit-provenance-disabled";
        final PipelineElement newPipeline =
                TestMetamodelUtil.createPipelineWithType(name, BOOZ_ALLEN_PACKAGE,
                        "data-flow", "default");

        final StepElement step = new StepElement();
        step.setName("step-0");
        step.setType("ingest");

        final ProvenanceElement provenanceElement = new ProvenanceElement();
        provenanceElement.setEnabled(false);
        step.setProvenance(provenanceElement);

        final StepDataBindingElement inbound = new StepDataBindingElement();
        inbound.setType("messaging");
        inbound.setChannelName("unit-test-inbound");
        inbound.setChannelType("topic");
        step.setInbound(inbound);

        final StepDataBindingElement outbound = new StepDataBindingElement();
        outbound.setType("messaging");
        outbound.setChannelName("unit-test-outbound");
        outbound.setChannelType("queue");
        step.setOutbound(outbound);

        newPipeline.addStep(step);

        pipelineFile = savePipelineToFile(newPipeline);
    }
    
    @Given("a valid pipeline with a step that has no explicit alerting")
    public void aValidPipelineWithMessaging() throws IOException {
        String name = "alerting-default";
        final PipelineElement newPipeline =
                TestMetamodelUtil.createPipelineWithType(name, BOOZ_ALLEN_PACKAGE,
                        "data-flow", "default");

        final StepElement step = new StepElement();
        step.setName("step-0");
        step.setType("ingest");
        
        final AlertingElement alertElement = new AlertingElement();
        step.setAlerting(alertElement);

        newPipeline.addStep(step);

        pipelineFile = savePipelineToFile(newPipeline);
    }
    
    @Given("a valid pipeline with a step that has alerting disabled")
    public void aValidPipelineWithMessagingOFF() throws IOException {
        String name = "alerting-disabled";
        final PipelineElement newPipeline =
                TestMetamodelUtil.createPipelineWithType(name, BOOZ_ALLEN_PACKAGE,
                        "data-flow", "default");

        final StepElement step = new StepElement();
        step.setName("step-0");
        step.setType("ingest");
        
        final AlertingElement alertElement = new AlertingElement();
        alertElement.setEnabled(false);
        step.setAlerting(alertElement);

        newPipeline.addStep(step);

        pipelineFile = savePipelineToFile(newPipeline);
    }

    @Given("an otherwise valid data delivery pipeline with a step that has model lineage defined")
    public void anOtherwiseValidDataDeliveryPipelineWithModelLineageEnabled() throws Exception {
        String name = "model-lineage-defined";
        final PipelineElement newPipeline = TestMetamodelUtil.createPipelineWithType(name, BOOZ_ALLEN_PACKAGE, "data-flow", "default");

        final StepElement step = new StepElement();
        step.setName("step-0");
        step.setType("ingest");

        final ModelLineageElement modelLineage = new ModelLineageElement();
        modelLineage.setEnabled(false);
        step.setModelLineage(modelLineage);

        newPipeline.addStep(step);

        pipelineFile = savePipelineToFile(newPipeline);
    }

    @Given("a valid machine learning pipeline and a valid data flow pipeline")
    public void a_valid_machine_learning_pipeline_and_a_valid_data_flow_pipeline() throws Exception {
        //Data Flow Pipeline
        String dataFlowPipelineName = "DataFlowPipeline";
        PipelineElement newDataFlowPipeline = TestMetamodelUtil.createPipelineWithType(dataFlowPipelineName, BOOZ_ALLEN_PACKAGE, "data-flow", "test-implementation");
        List<Step> dataFlowSteps = createStepsForDataFlowPipeline(2);
        dataFlowSteps.forEach((step) -> newDataFlowPipeline.addStep(step));
        savePipelineToFile(newDataFlowPipeline);

        //Machine Learning Pipeline
        String machineLearningPipelineName = "MachineLearningPipeline";
        PipelineElement newMachineLearningPipeline = TestMetamodelUtil.createPipelineWithType(machineLearningPipelineName, BOOZ_ALLEN_PACKAGE, "machine-learning", "test-implementation");
        List<Step> machineLearningSteps = createStepsForMachineLearningPipeline(3);
        machineLearningSteps.forEach((step) -> newMachineLearningPipeline.addStep(step));
        savePipelineToFile(newMachineLearningPipeline);
    }

    @Given("A valid data-flow PySpark pipeline with a RDBMS ingest step")
    public void a_valid_data_flow_pyspark_pipeline_with_a_RDBMS_ingest_step() throws Exception {
        PipelineElement newDataFlowPipeline = TestMetamodelUtil.createPipelineWithType(DATA_FLOW_PIPELINE, BOOZ_ALLEN_PACKAGE, "data-flow", "data-delivery-pyspark");
        List<Step> dataFlowSteps = createStepForPySparkRDMSDataFlowPipeline();
        dataFlowSteps.forEach(newDataFlowPipeline::addStep);
        this.pipelineFile = savePipelineToFile(newDataFlowPipeline);
    }

    @Given("a valid machine learning pipeline with a training step")
    public void a_valid_machine_learning_pipeline_with_a_training_step() throws Exception {
        //Machine Learning Pipeline
        String machineLearningPipelineName = "MachineLearningPipeline";
        PipelineElement newMachineLearningPipeline = TestMetamodelUtil.createPipelineWithType(machineLearningPipelineName, BOOZ_ALLEN_PACKAGE, "machine-learning", "test-implementation");
        List<Step> machineLearningSteps = createStepsForMachineLearningPipeline(1);
        machineLearningSteps.forEach((step) -> newMachineLearningPipeline.addStep(step));
        pipelineFile = savePipelineToFile(newMachineLearningPipeline);
    }	

    @Given("a valid machine learning training pipeline with an airflow executionHelper")
    public void a_valid_machine_learning_pipeline_with_an_airflow_executionHelper() throws Exception {
        //Machine Learning Pipeline
        String machineLearningPipelineName = "MachineLearningPipeline";
        PipelineElement newMachineLearningPipeline = TestMetamodelUtil.createPipelineWithType(machineLearningPipelineName, BOOZ_ALLEN_PACKAGE, "machine-learning", "test-implementation");

        PipelineTypeElement type = (PipelineTypeElement) newMachineLearningPipeline.getType();
        executionHelpers = new ArrayList<>();
        executionHelpers.add("airflow");
        type.setExecutionHelpers(executionHelpers);

        List<Step> machineLearningSteps = createStepsForMachineLearningPipeline(1);
        machineLearningSteps.forEach((step) -> newMachineLearningPipeline.addStep(step));
        savePipelineToFile(newMachineLearningPipeline);
    }		

    @Given("a valid machine learning pipeline with an airflow executionHelper and no training step")
    public void a_valid_machine_learning_pipeline_with_an_airflow_executionHelper_and_no_training_step() throws Exception {
        //Machine Learning Pipeline
        String machineLearningPipelineName = "MachineLearningPipeline";
        PipelineElement newMachineLearningPipeline = TestMetamodelUtil.createPipelineWithType(machineLearningPipelineName, BOOZ_ALLEN_PACKAGE, "machine-learning", "test-implementation");

        PipelineTypeElement type = (PipelineTypeElement) newMachineLearningPipeline.getType();
        executionHelpers = new ArrayList<>();
        executionHelpers.add("airflow");
        type.setExecutionHelpers(executionHelpers);

        List<Step> machineLearningSteps = createInferenceStepForMachineLearningPipeline();
        machineLearningSteps.forEach((step) -> newMachineLearningPipeline.addStep(step));
        savePipelineToFile(newMachineLearningPipeline);
    }	

    @Given("a valid machine learning pipeline")
    public void a_valid_machine_learning_pipeline() throws Exception {
        String name = "versioning-enabled";
        PipelineElement newPipeline = TestMetamodelUtil.createPipelineWithType(name, BOOZ_ALLEN_PACKAGE, "machine-learning", "default");

        VersioningElement versioningElement = new VersioningElement();
        PipelineTypeElement type = (PipelineTypeElement) newPipeline.getType();
        type.setVersioning(versioningElement);

        pipelineFile = savePipelineToFile(newPipeline);
    }

    @Given("an otherwise valid machine learning pipeline with data lineage enabled")
    public void anOtherwiseValidMachineLearningPipelineWithDataLineageEnabled() throws Exception {
        String name = "data-lineage-enabled";
        PipelineElement newPipeline = TestMetamodelUtil.createPipelineWithType(name, BOOZ_ALLEN_PACKAGE, "machine-learning", "default");

        newPipeline.setDataLineage(true);

        pipelineFile = savePipelineToFile(newPipeline);
    }

    @Given("a valid machine learning pipeline with versioning disabled")
    public void a_valid_machine_learning_pipeline_with_versioning_disabled() throws Exception {
        String name = "versioning-disabled";
        PipelineElement newPipeline = TestMetamodelUtil.createPipelineWithType(name, BOOZ_ALLEN_PACKAGE, "machine-learning", "default");

        VersioningElement versioningElement = new VersioningElement();
        versioningElement.setEnabled(false);
        PipelineTypeElement type = (PipelineTypeElement) newPipeline.getType();
        type.setVersioning(versioningElement);

        pipelineFile = savePipelineToFile(newPipeline);
    }

    @Given("platforms are specified for a pipeline")
    public void platforms_are_specified_for_a_pipeline() throws Exception {
        // generate random number of platforms for test
        testPlatforms = new ArrayList<>();
        int numberOfPlatforms = new Random().nextInt(5) + 1;
        for (int i = 0; i < numberOfPlatforms; i++) {
            PlatformElement platform = new PlatformElement();
            platform.setName(RandomStringUtils.randomAlphabetic(5));
            testPlatforms.add(platform);
        }

        String name = "pipeline-with-platforms";
        PipelineElement newPipeline = TestMetamodelUtil.createPipelineWithType(name, BOOZ_ALLEN_PACKAGE, "data-flow", "default");

        PipelineTypeElement pipelineType = (PipelineTypeElement) newPipeline.getType();
        pipelineType.setPlatforms(testPlatforms);
        newPipeline.setType(pipelineType);

        pipelineFile = savePipelineToFile(newPipeline);
    }

    @Given("a valid data delivery pipeline with lineage undefined")
    public void a_valid_data_delivery_pipeline_with_lineage_undefined() throws Exception {
        String name = "lineage-undefined";
        PipelineElement newPipeline = TestMetamodelUtil.createPipelineWithType(name, BOOZ_ALLEN_PACKAGE, "data-flow", "default");

        StepElement step = new StepElement();
        step.setName("step-0");
        step.setType("ingest");

        newPipeline.addStep(step);

        pipelineFile = savePipelineToFile(newPipeline);
    }

    @Given("a valid data delivery pipeline with lineage enabled")
    public void a_valid_data_delivery_pipeline_with_lineage_enabled() throws Exception {
        String name = "lineage-enabled";
        PipelineElement newPipeline = TestMetamodelUtil.createPipelineWithType(name, BOOZ_ALLEN_PACKAGE, "data-flow", "default");
        newPipeline.setDataLineage(true);

        StepElement step = new StepElement();
        step.setName("step-0");
        step.setType("ingest");

        newPipeline.addStep(step);

        pipelineFile = savePipelineToFile(newPipeline);
    }

    @Given("a valid machine learning pipeline with data profiling undefined")
    public void a_valid_machine_learning_pipeline_with_data_profiling_undefined() throws Exception {
        String name = "profiling-undefined";
        PipelineElement newPipeline = TestMetamodelUtil.createPipelineWithType(name, BOOZ_ALLEN_PACKAGE, "machine-learning", "default");

        StepElement step = new StepElement();
        step.setName("step-0");
        step.setType("ingest");

        newPipeline.addStep(step);

        pipelineFile = savePipelineToFile(newPipeline);
    }

    @Given("a valid machine learning pipeline with data profiling set to true")
    public void a_valid_machine_learning_pipeline_with_data_profiling_set_to_true() throws Exception {
        String name = "profiling-true";
        PipelineElement newPipeline = TestMetamodelUtil.createPipelineWithType(name, BOOZ_ALLEN_PACKAGE, "machine-learning", "default");
        StepElement step = new StepElement();
        step.setName("step-0");
        step.setType("ingest");

        DataProfilingElement dataProfiling = new DataProfilingElement();
        dataProfiling.setEnabled(true);
        step.setDataProfiling(dataProfiling);

        newPipeline.addStep(step);

        pipelineFile = savePipelineToFile(newPipeline);
    }

    @Given("a valid machine learning pipeline with data profiling set to false")
    public void a_valid_machine_learning_pipeline_with_data_profiling_set_to_false() throws Exception {
        String name = "profiling-false";
        PipelineElement newPipeline = TestMetamodelUtil.createPipelineWithType(name, BOOZ_ALLEN_PACKAGE, "machine-learning", "default");
        StepElement step = new StepElement();
        step.setName("step-0");
        step.setType("ingest");

        DataProfilingElement dataProfiling = new DataProfilingElement();
        dataProfiling.setEnabled(false);
        step.setDataProfiling(dataProfiling);

        newPipeline.addStep(step);

        pipelineFile = savePipelineToFile(newPipeline);
    }

    @Given("a valid machine learning pipeline with a training step and modelLineage is enabled")
    public void a_valid_machine_learning_training_pipeline_with_lineage_enabled() throws Exception {
        //Machine Learning Pipeline
        String machineLearningPipelineName = "modelLineage-true";
        PipelineElement newMachineLearningPipeline = TestMetamodelUtil.createPipelineWithType(machineLearningPipelineName, BOOZ_ALLEN_PACKAGE, "machine-learning", "default");
        
        StepElement step = new StepElement();
        step.setName("step-0");
        step.setType("training");

        ModelLineageElement modelLineage = new ModelLineageElement();
        modelLineage.setEnabled(true);
        step.setModelLineage(modelLineage);

        newMachineLearningPipeline.addStep(step);

        pipelineFile = savePipelineToFile(newMachineLearningPipeline);
    }

    @When("pipelines are read")
    public void pipelines_are_read() {
        encounteredException = null;

        try {
            pipeline = JsonUtils.readAndValidateJson(pipelineFile, PipelineElement.class);
            pipeline.validate();
            assertNotNull("Could not read pipeline file!", pipeline);

        } catch (GenerationException e) {
            encounteredException = e;
        }
    }

    @When("data flow artifact ids are requested")
    public void data_flow_artifact_ids_are_requested() {
        encounteredException = null;

        try {
            File dataFlowPipelineFile = getPipelineFileByName("DataFlowPipeline");
            dataFLowPipeline = JsonUtils.readAndValidateJson(dataFlowPipelineFile, PipelineElement.class);
            assertNotNull("Could not read data flow pipeline file!", dataFLowPipeline);

            File machineLearningPipelineFile = getPipelineFileByName("MachineLearningPipeline");
            machineLearningPipeline = JsonUtils.readAndValidateJson(machineLearningPipelineFile, PipelineElement.class);
            assertNotNull("Could not read machine learning pipeline file!", machineLearningPipeline);
        } catch (GenerationException e) {
            encounteredException = e;
        }
    }

    @When("machine learning artifact ids are requested")
    public void machine_learning_artifact_ids_are_requested() {
        encounteredException = null;

        try {
            File machineLearningPipelineFile = getPipelineFileByName("MachineLearningPipeline");
            machineLearningPipeline = JsonUtils.readAndValidateJson(machineLearningPipelineFile, PipelineElement.class);
            assertNotNull("Could not read machine learning pipeline file!", machineLearningPipeline);
        } catch (GenerationException e) {
            encounteredException = e;
        }
    }	

    @When("a pipeline is configured for messaging without a channel name")
    public void aPipelineIsConfiguredForMessagingWithoutAChannelName() throws Exception {
        testStep.setName("step-0");
        testStep.setType("ingest");

        final StepDataBindingElement stepDataBindingElement = new StepDataBindingElement();
        stepDataBindingElement.setChannelType("messaging");

        testStep.setInbound(stepDataBindingElement);
        testStep.validate();
    }

    @When("machine learning pipelines are validated")
    public void machine_learning_pipelines_are_validated() throws Exception {
        pipeline = JsonUtils.readAndValidateJson(pipelineFile, PipelineElement.class);
        pipeline.validate();
    }

    @Then("a valid pipeline is available can be looked up by the name {string} and {string}")
    public void a_valid_pipeline_is_available_can_be_looked_up_by_the_name_and(String expectedName,
                                                                               String expectedPackageName) {
        assertPipelineFoundByNameAndPackage(expectedName, expectedPackageName);
    }

    @Then("the generator throws an exception about invalid metamodel information")
    public void the_generator_throws_an_exception_about_invalid_metamodel_information() {
        assertNotNull("A GenerationException should have been thrown!", encounteredException);
    }

    @Then("{int} are found in the pipeline when it is looked up by the name {string} and {string}")
    public void are_found_in_the_pipeline_when_it_is_looked_up_by_the_name_and(Integer expectedNumberOfSteps,
                                                                               String expectedName, String expectedPackageName) {
        assertPipelineFoundByNameAndPackage(expectedName, expectedPackageName);
        List<? extends Step> foundSteps = pipeline.getSteps();
        assertEquals("Unexpected number of steps found!", expectedNumberOfSteps.intValue(), foundSteps.size());
    }

    @Then("the pipeline is created with the default provenance creation")
    public void thePipelineIsCreatedWithTheDefaultProvenanceCreation() {
        assertPipelineFoundByNameAndPackage("explicit-provenance", BOOZ_ALLEN_PACKAGE);
        final Provenance provenance = pipeline.getSteps().get(0).getProvenance();
        assertNotNull("Provenance not added by default", provenance);
        assertTrue("Default provenance configuration is defaulted to disabled", provenance.isEnabled());
    }

    @Then("the pipeline is created without including provenance creation")
    public void thePipelineIsCreatedWithoutIncludingProvenanceCreation() {
        assertPipelineFoundByNameAndPackage("explicit-provenance-disabled", BOOZ_ALLEN_PACKAGE);
        final Provenance provenance = pipeline.getSteps().get(0).getProvenance();
        assertNotNull("Provenance added when explicitly disabled", provenance);
        assertFalse("Explicitly disabled provenance configuration is enabled", provenance.isEnabled());
    }

    @Then("the pipeline step is created with alerting enabled")
    public void thePipelineStepIsCreatedWithAlertingEnabled() {
        assertPipelineFoundByNameAndPackage("alerting-default", BOOZ_ALLEN_PACKAGE);
        final Alerting alert = pipeline.getSteps().get(0).getAlerting();
        assertNotNull("Alerting not added by default", alert);
        assertTrue("Alerting should be turned on by default.", alert.isEnabled());
    }

    @Then("the pipeline step is created with alerting disabled")
    public void thePipelineStepIsCreatedWithAlertingDisabled() {
        assertPipelineFoundByNameAndPackage("alerting-disabled", BOOZ_ALLEN_PACKAGE);
        final Alerting alert = pipeline.getSteps().get(0).getAlerting();
        assertNotNull("Alerting not added when explicitly included", alert);
        assertFalse("Alerting should be turned off in this situation.", alert.isEnabled());
    }
    
    @Then("only the data flow artifact ids are retrieved")
    public void only_the_data_flow_artifact_ids_are_retrieved() {
        PipelineContext pipelineContext = new PipelineContext();
        List<Pipeline> pipelines = new ArrayList<>();
        pipelines.add(dataFLowPipeline);
        pipelines.add(machineLearningPipeline);
        pipelineContext.setPipelineStrategy(new DataFlowStrategy(pipelines));
        List<String> artifactIds = pipelineContext.getArtifactIds();
        assertNotNull(artifactIds);

        assertEquals("Number of data flow artifact ids was not one", 1, artifactIds.size());
        assertEquals("Incorrect artifact id found", "data-flow-pipeline", artifactIds.get(0));
    }

    @Then("step artifact ids for data flow are retrieved")
    public void step_artifact_ids_for_data_flow_are_retrieved() {
        PipelineContext pipelineContext = new PipelineContext();
        List<Pipeline> pipelines = new ArrayList<>();
        pipelines.add(dataFLowPipeline);
        pipelines.add(machineLearningPipeline);
        pipelineContext.setPipelineStrategy(new DataFlowStrategy(pipelines));
        List<String> stepArtifactIds = pipelineContext.getStepArtifactIds();

        assertNotNull(stepArtifactIds);
        assertEquals("Number of data flow step artifact ids was not two", 2, stepArtifactIds.size());
        assertEquals("Incorrect artifact id found", "step-0", stepArtifactIds.get(0));
        assertEquals("Incorrect artifact id found", "step-1", stepArtifactIds.get(1));
    }

    @Then("step artifact ids for machine learning are retrieved")
    public void step_artifact_ids_for_machine_learning_are_retrieved() {
        PipelineContext pipelineContext = new PipelineContext();
        List<Pipeline> pipelines = new ArrayList<>();
        pipelines.add(dataFLowPipeline);
        pipelines.add(machineLearningPipeline);
        pipelineContext.setPipelineStrategy(new MachineLearningStrategy(pipelines));
        List<String> artifactIds = pipelineContext.getArtifactIds();
        List<String> stepArtifactIds = pipelineContext.getStepArtifactIds();

        assertNotNull(artifactIds);
        assertNotNull(stepArtifactIds);

        assertEquals("Number of machine learning artifact ids was not one", 1, artifactIds.size());
        assertEquals("Number of machine learning step artifact ids was not three", 3, stepArtifactIds.size());
        assertEquals("Incorrect artifact id found", "step-2", stepArtifactIds.get(2));
    }

    @Then("the pipeline is created with versioning")
    public void the_pipeline_is_created_with_versioning() {
        assertPipelineFoundByNameAndPackage("versioning-enabled", BOOZ_ALLEN_PACKAGE);
        Versioning versioning = pipeline.getType().getVersioning();

        assertNotNull("Versioning was not added by default", versioning);
        assertTrue("Default versioning configuration is disabled when it should be enabled", versioning.isEnabled());
    }

    @Then("the pipeline is created without versioning")
    public void the_pipeline_is_created_without_versioning() {
        assertPipelineFoundByNameAndPackage("versioning-disabled", BOOZ_ALLEN_PACKAGE);
        Versioning versioning = pipeline.getType().getVersioning();

        assertNotNull("Explicitly disabled versioning configuration was not added", versioning);
        assertFalse("Explicitly disabled versioning configuration is enabled", versioning.isEnabled());
    }

    @Then("an error is thrown")
    public void anErrorIsThrown() {
        assertTrue("No error thrown when channel name is not configured for messaging",
                StepDataBindingElement.messageTracker.hasErrors());
    }

    @Then("the pipeline is created with the specified platforms")
    public void the_pipeline_is_created_with_the_specified_platforms() {
        assertPipelineFoundByNameAndPackage("pipeline-with-platforms", BOOZ_ALLEN_PACKAGE);
        List<Platform> platforms = pipeline.getType().getPlatforms();
        assertEquals("Unexpected number of platforms found", testPlatforms.size(), platforms.size());

        List<String> platformNames = platforms.stream()
                .map(Platform::getName)
                .collect(Collectors.toList());

        for (Platform expectedPlatform : testPlatforms) {
            assertTrue("Expected platform not found", platformNames.contains(expectedPlatform.getName()));
        }
    }

    // MLFLOW artifact generation tests
    @Then("the machine learning pipeline yields mlflow artifacts")
    public void the_machine_learning_pipeline_yields_mlflow_artifacts() {
        PipelineContext pipelineContext = new PipelineContext();
        List<Pipeline> pipelines = new ArrayList<>();
        pipelines.add(machineLearningPipeline);
        MachineLearningStrategy machineLearningStrategy = new MachineLearningStrategy(pipelines);
        assertTrue("Machine learning pipeline did not trigger mlflow artifact generation",
                machineLearningStrategy.isMlflowNeeded());
    }

    @Then("the data flow pipeline does not yield mlflow artifacts")
    public void the_data_flow_pipeline_does_not_yield_mlflow_artifacts() {
        PipelineContext pipelineContext = new PipelineContext();
        List<Pipeline> pipelines = new ArrayList<>();
        pipelines.add(dataFLowPipeline);
        MachineLearningStrategy machineLearningStrategy = new MachineLearningStrategy(pipelines);
        assertFalse("Data flow pipeline triggered mlflow artifact generation, but it should not",
                machineLearningStrategy.isMlflowNeeded());
    }

    @Then("the pipeline is created with the specified persist mode")
    public void the_pipeline_is_created_with_the_specified_persist_save_mode() {
        assertPipelineFoundByNameAndPackage("persist-mode-validation", BOOZ_ALLEN_PACKAGE);
        Persist persist = pipeline.getSteps().get(0).getPersist();
        assertFalse("Persist mode not found", StringUtils.isEmpty(persist.getMode()));
        assertEquals("Unexpected persist mode found", "test-mode", persist.getMode());
    }

    @Then("the pipeline is created with the specified persist type {string}")
    public void the_pipeline_is_created_with_the_specified_persist_save_type(String expectedType) {
        assertNotNull("No pipeline found", pipeline);

        List<StepElement> steps = (List<StepElement>)(List<?>) pipeline.getSteps();
        assertTrue("No steps were found in the pipeline", CollectionUtils.isNotEmpty(steps));
        Persist persist = steps.get(0).getPersist();
        assertNotNull("Persist object not found", persist);
        String realType = persist.getType();
        assertFalse("Persist type not found", StringUtils.isEmpty(realType));
        assertEquals("Unexpected persist type found", expectedType, realType);
    }

    @Then("the pipeline is created with lineage disabled")
    public void the_pipeline_is_created_with_lineage_disabled() {
        assertPipelineFoundByNameAndPackage("lineage-undefined", BOOZ_ALLEN_PACKAGE);
        assertFalse("Expected lineage to be enabled for the pipeline.", pipeline.getDataLineage());
    }

    @Then("the pipeline is created with lineage enabled")
    public void the_pipeline_is_created_with_lineage_enabled() {
        assertPipelineFoundByNameAndPackage("lineage-enabled", BOOZ_ALLEN_PACKAGE);
        assertTrue("Expected lineage to be enabled for the pipeline.", pipeline.getDataLineage());
    }

    @Then("the pipeline is created without data profiling")
    public void the_pipeline_is_created_without_data_profiling() {
        assertPipelineFoundByNameAndPackage("profiling-undefined", BOOZ_ALLEN_PACKAGE);
        final DataProfiling dataProfiling = pipeline.getSteps().get(0).getDataProfiling();
        assertNull("Expected data profiling to be undefined for the machine learning pipeline.", dataProfiling);
    }

    // AIRFLOW artifact generation test
    @Then("the machine learning pipeline does not yield airflow artifacts")
    public void the_machine_learning_pipeline_does_not_yield_airflow_artifacts() {
        List<Pipeline> pipelines = new ArrayList<>();
        pipelines.add(machineLearningPipeline);
        MachineLearningStrategy machineLearningStrategy = new MachineLearningStrategy(pipelines);
        assertFalse("Machine learning pipeline did not trigger airflow artifact generation",
                machineLearningStrategy.isAirflowNeeded());
    }	

    @Then("the machine learning pipeline yields airflow artifacts")
    public void the_machine_learning_pipeline_yields_airflow_artifacts() {
        List<Pipeline> pipelines = new ArrayList<>();
        pipelines.add(machineLearningPipeline);
        MachineLearningStrategy machineLearningStrategy = new MachineLearningStrategy(pipelines);
        assertTrue("Machine learning pipeline triggered airflow artifact generation",
                machineLearningStrategy.isAirflowNeeded());
    }	
    @Then("the training step is created with modelLineage disabled") 
    public void machine_learning_training_is_created_with_model_lineage() {
        BasePipelineDecorator decoratedPipeline = new BasePipelineDecorator(pipeline);
        BaseStepDecorator step = decoratedPipeline.getSteps().get(0);
        assertNull("Expected lineage to be undefined for the Machine Learning pipeline with training step.",
                step.getModelLineage());
        assertFalse("Expected undefinied lineage to default to false for step", step.isModelLineageEnabled());
        assertFalse("Expected undefinied lineage to default to false for pipeline", decoratedPipeline.isModelLineageSupportNeeded());
    }

    @Then("the training step is created with modelLineage enabled")
    public void machine_learning_training_is_created_without_model_lineage() {
        assertTrue("Expected lineage to be enabled for the Machine Learning pipeline with training step.",
                pipeline.getSteps().get(0).getModelLineage().isEnabled());
    }
    @Then("a common step parent is created")
    public void aCommonStepParentIsCreated() throws IOException {
        String file = projectDir.resolve(ABSTRACT_DATA_ACTION_IMPL_PY).toString();
        assertTrue(
                "Could not find common parent AbstractDataActionImpl",
                parseFileForRegex(file, ABSTRACT_DATA_ACTION_IMPL_INHERIT_REGEX
                ));

        file = projectDir.resolve(ABSTRACT_PIPELINE_STEP_PY).toString();
        assertTrue("Could not find common parent AbstractPipelineStep",
                parseFileForRegex(file, ABSTRACT_PIPELINE_STEP_INHERIT_REGEX
                ));
    }

    @Then("the common step parent is customizable")
    public void theCommonStepParentIsCustomizable() throws IOException {
        String file = projectDir.resolve(ABSTRACT_DATA_ACTION_IMPL_PY).toString();
        assertTrue(
                "Could not customize step parent",
                parseFileForRegex(file, DO_MODIFY_REGEX)
        );
    }

    @Then("there are validation error messages")
    public void thereAreValidationErrorMessages() {
        Assert.assertTrue("Pipeline validation did not add error messages!", MessageTracker.getInstance().hasErrors());
    }

    private boolean parseFileForRegex(String dir, String regex) throws IOException {
        boolean found = false;
        File file = new File(dir);
        if (file.exists() && !file.isDirectory()) {
            Charset charset = StandardCharsets.UTF_8;
            String fileContent = new String(Files.readAllBytes(file.toPath()), charset);
            Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(fileContent);
            found = matcher.find();
        }

        return found;
    }

    private void assertPipelineFoundByNameAndPackage(String expectedName, String expectedPackageName) {
        if (pipeline == null) {
            throw new RuntimeException("Problem reading pipeline file:", encounteredException);
        }

        assertEquals(expectedName, pipeline.getName());
        assertEquals(expectedPackageName, pipeline.getPackage());
    }

    private List<Step> createStepsForDataFlowPipeline(Integer numberOfSteps) {
        List<Step> steps = new ArrayList<>();
        for (int i = 0; i < numberOfSteps; i++) {
            StepElement step = new StepElement();
            step.setName("step-" + i);
            step.setType("ingest");

            PersistElement persist = new PersistElement();
            persist.setType("delta-lake");
            step.setPersist(persist);

            StepDataBindingElement inbound = new StepDataBindingElement();
            inbound.setType("messaging");
            inbound.setChannelName("unit-test-inbound");
            inbound.setChannelType("topic");
            step.setInbound(inbound);

            StepDataBindingElement outbound = new StepDataBindingElement();
            outbound.setType("messaging");
            outbound.setChannelName("unit-test-outbound");
            outbound.setChannelType("queue");
            step.setOutbound(outbound);

            for (int j = 0; j < RandomUtils.nextInt(0, 4); j++) {
                ConfigurationItemElement configurationItem = new ConfigurationItemElement();
                configurationItem.setKey(RandomStringUtils.randomAlphanumeric(3));
                configurationItem.setValue(RandomStringUtils.randomAlphanumeric(10));
                step.addConfigurationItem(configurationItem);
            }

            steps.add(step);
        }

        return steps;
    }

    private List<Step> createStepsForMachineLearningPipeline(Integer numberOfSteps) {
        List<Step> steps = new ArrayList<>();

        for (int i = 0; i < numberOfSteps; i++) {
            StepElement trainingStep = new StepElement();
            trainingStep.setName("step-" + i);
            trainingStep.setType("training");
            steps.add(trainingStep);
        }

        return steps;
    }


    private List<Step> createInferenceStepForMachineLearningPipeline() {
        List<Step> steps = new ArrayList<>();

        StepElement inferenceStep = new StepElement();
        inferenceStep.setName("step");
        inferenceStep.setType("inference");
        steps.add(inferenceStep);

        return steps;
    }

    private List<Step> createStepForPySparkRDMSDataFlowPipeline() {
        List<Step> steps = new ArrayList<>();
        StepElement step = new StepElement();
        step.setName("IngestData" );
        step.setType("synchronous");

        PersistElement persist = new PersistElement();
        persist.setType("rdbms");
        step.setPersist(persist);

        ConfigurationItemElement configurationItem = new ConfigurationItemElement();
        configurationItem.setKey("targetedPipeline");
        configurationItem.setValue("DataFlowPipeline");
        step.addConfigurationItem(configurationItem);

        steps.add(step);

        return steps;
    }

}
