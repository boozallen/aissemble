package com.boozallen.aissemble.data.lineage;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Data Lineage
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.data.lineage.config.ConfigUtil;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.openlineage.client.OpenLineage;

import java.util.*;

import static org.junit.Assert.*;

public class BuildOpenLineageSteps {

    RunFacet testRunFacet;
    JobFacet testJobFacet;
    DatasetFacet testDatasetFacet;

    OpenLineage.DatasetFacet defaultDatasetFacet;
    OpenLineage.JobFacet JobFacet;
    OpenLineage.RunFacet RunFacet;

    Run testRun;
    OpenLineage.Run openLineageRun;

    Job testJob;
    OpenLineage.Job openLineageJob;

    InputDataset testInputDataset;
    OpenLineage.InputDataset openLineageInputDataset;
    InputDataset testInputDatasetWithInputFacet;
    OpenLineage.InputDataset openLineageInputDatasetWithInputFacet;

    OutputDataset testOutputDataset;
    OpenLineage.OutputDataset openLineageOutputDataset;

    OutputDataset testOutputDatasetWithOutputFacet;
    OpenLineage.OutputDataset openLineageOutputDatasetWithOutputFacet;

    RunEvent testRunEvent;
    OpenLineage.RunEvent openLineageRunEvent;

    Exception caughtException;

    @Given("a valid Run Facet object")
    public void a_valid_run_facet_object() {
        testRunFacet = new TestRunFacet();
    }

    @Given("a valid Job Facet object")
    public void a_valid_job_facet_object() {
        testJobFacet = new TestJobFacet();
    }

    @Given("a valid Dataset Facet object")
    public void a_valid_dataset_facet_object() {
        testDatasetFacet = new TestDatasetFacet();
        assertNotNull(testDatasetFacet);
    }

    @When("the Facet's function to build an OpenLineage DatasetFacet is called")
    public void the_facet_s_function_to_build_an_open_lineage_dataset_facet_is_called() {
        testDatasetFacet.setSchemaUrl("https://openlineage.io/spec/1-0-5/OpenLineage.json#/$defs/DatasetFacet");
        defaultDatasetFacet = testDatasetFacet.getOpenLineageFacet();
    }

    @Then("a valid OpenLineage DatasetFacet will be created with its fields populated")
    public void a_valid_open_lineage_dataset_facet_will_be_created_with_its_fields_populated() {
        assertNotNull(defaultDatasetFacet);
        assertEquals(testDatasetFacet.getProducer(), defaultDatasetFacet.get_producer());
        assertEquals(testDatasetFacet.getSchemaUrl(), defaultDatasetFacet.get_schemaURL().toString());
    }

    @When("the Facet's function to build an OpenLineage JobFacet is called")
    public void the_facet_s_function_to_build_an_open_lineage_job_facet_is_called() {
        testJobFacet.setSchemaUrl("https://openlineage.io/spec/1-0-5/OpenLineage.json#/$defs/JobFacet");
        JobFacet = testJobFacet.getOpenLineageFacet();
    }

    @Then("a valid OpenLineage JobFacet will be created with its fields populated")
    public void a_valid_open_lineage_job_facet_will_be_created_with_its_fields_populated() {
        assertNotNull(JobFacet);
        assertEquals(testJobFacet.getProducer(), JobFacet.get_producer());
        assertEquals(testJobFacet.getSchemaUrl(), JobFacet.get_schemaURL().toString());
    }

    @When("the Facet's function to build an OpenLineage RunFacet is called")
    public void the_facet_s_function_to_build_an_open_lineage_run_facet_is_called() {
        testRunFacet.setSchemaUrl("https://openlineage.io/spec/1-0-5/OpenLineage.json#/$defs/RunFacet");
        RunFacet = testRunFacet.getOpenLineageFacet();
    }

    @Then("a valid OpenLineage RunFacet will be created with its fields populated")
    public void a_valid_open_lineage_run_facet_will_be_created_with_its_fields_populated() {
        assertNotNull(RunFacet);
        assertEquals(testRunFacet.getProducer(), RunFacet.get_producer());
        assertEquals(testRunFacet.getSchemaUrl(), RunFacet.get_schemaURL().toString());
    }

    @Given("a valid Run object")
    public void a_valid_run_object() {
        testRun = new Run(UUID.randomUUID(), createTestFacets("testRunFacet", new TestRunFacet()));
    }

    @When("the Run's function to build an OpenLineage object is called")
    public void the_run_s_function_to_build_an_open_lineage_object_is_called() {
        openLineageRun = testRun.getOpenLineageRun();
    }

    @Then("a valid OpenLineage Run will be created with its fields populated")
    public void a_valid_open_lineage_run_will_be_created_with_its_fields_populated() {
        assertNotNull(openLineageRun);
        assertEquals(testRun.getRunId(), openLineageRun.getRunId());
        RunFacet expectedFacet = testRun.getFacets().get("testRunFacet");
        OpenLineage.RunFacet actualFacet =
                (OpenLineage.RunFacet) openLineageRun.getFacets().getAdditionalProperties().values().toArray()[0];
        assertEquals(expectedFacet.getProducer(), actualFacet.get_producer());
    }

    @Given("a valid Job object")
    public void a_valid_job_object() {
        testJob = new Job(
                "test_job",
                createTestFacets("testJobFacet", new TestJobFacet()),
                "test_namespace"
        );
    }

    @When("the Job's function to build an OpenLineage object is called")
    public void the_job_s_function_to_build_an_open_lineage_object_is_called() {
        openLineageJob = testJob.getOpenLineageJob();
    }

    @Then("a valid OpenLineage Job will be created with its fields populated")
    public void a_valid_open_lineage_job_will_be_created_with_its_fields_populated() {
        assertNotNull(openLineageJob);
        assertEquals(testJob.getName(), openLineageJob.getName());
        JobFacet expectedFacet = testJob.getFacets().get("testJobFacet");
        OpenLineage.JobFacet actualFacet =
                (OpenLineage.JobFacet) openLineageJob.getFacets().getAdditionalProperties().values().toArray()[0];
        assertEquals(expectedFacet.getProducer(), actualFacet.get_producer());
    }

    @Given("a valid Input Dataset object named {string}")
    @When("a valid Input Dataset object named {string} is declared")
    public void a_valid_input_dataset_object_named(String name) {
        try {
            testInputDataset = new InputDataset(name, createTestFacets("testInputDatasetFacet", new TestDatasetFacet()));
        } catch (Exception e) {
            caughtException = e;
        }

    }

    @When("the Input Dataset's function to build an OpenLineage object is called")
    public void the_input_dataset_s_function_to_build_an_open_lineage_object_is_called() {
        openLineageInputDataset = testInputDataset.getOpenLineageDataset();
    }

    @Then("a valid OpenLineage Input Dataset will be created with its fields populated")
    public void a_valid_open_lineage_input_dataset_will_be_created_with_its_fields_populated() {
        assertNotNull(openLineageInputDataset);
        assertEquals(testInputDataset.getName(), openLineageInputDataset.getName());
        assertEquals(testInputDataset.getNamespace(), openLineageInputDataset.getNamespace());
        DatasetFacet expectedFacet = testInputDataset.getFacets().get("testInputDatasetFacet");
        OpenLineage.DatasetFacet actualFacet =
                (OpenLineage.DatasetFacet) openLineageInputDataset.getFacets().getAdditionalProperties().values().toArray()[0];
        assertEquals(expectedFacet.getProducer(), actualFacet.get_producer());
    }

    @Given("a valid Input Dataset object with an input facet")
    public void a_valid_input_dataset_object_with_a_defined_input_facet() {
        testInputDatasetWithInputFacet = new InputDataset("test_input_dataset", createTestFacets("testInputDatasetFacet", new TestDatasetFacet()), createTestFacets("testInputDatasetInputFacet", new TestInputDatasetFacet()));
    }

    @When("the Input Dataset's function to build an OpenLineage object is called with an input facet")
    public void the_input_dataset_s_function_to_build_an_open_lineage_object_is_called_with_an_input_facet() {
        openLineageInputDatasetWithInputFacet = testInputDatasetWithInputFacet.getOpenLineageDataset();
    }

    @Then("a valid OpenLineage Input Dataset will be created with its fields populated including the input facet")
    public void a_valid_open_lineage_input_dataset_will_be_created_with_its_fields_populated_including_the_input_facet() {
        assertNotNull(openLineageInputDatasetWithInputFacet);
        assertEquals(testInputDatasetWithInputFacet.getName(), openLineageInputDatasetWithInputFacet.getName());
        assertEquals(testInputDatasetWithInputFacet.getNamespace(), openLineageInputDatasetWithInputFacet.getNamespace());
        InputDatasetFacet expectedFacet = testInputDatasetWithInputFacet.getInputFacets().get("testInputDatasetInputFacet");
        OpenLineage.DatasetFacet actualFacet =
                (OpenLineage.DatasetFacet) openLineageInputDatasetWithInputFacet.getFacets().getAdditionalProperties().values().toArray()[0];
        assertEquals(expectedFacet.getProducer(), actualFacet.get_producer());
    }

    @Given("a valid Output Dataset object")
    public void a_valid_output_dataset_object() {
        testOutputDataset = new OutputDataset("test_output_dataset", createTestFacets("testOutputDatasetFacet", new TestDatasetFacet()));
    }

    @When("the Output Dataset's function to build an OpenLineage object is called")
    public void the_output_dataset_s_function_to_build_an_open_lineage_object_is_called() {
        openLineageOutputDataset = testOutputDataset.getOpenLineageDataset();
    }

    @Then("a valid OpenLineage Output Dataset will be created with its fields populated")
    public void a_valid_open_lineage_output_dataset_will_be_created_with_its_fields_populated() {
        assertNotNull(openLineageOutputDataset);
        assertEquals(testOutputDataset.getName(), openLineageOutputDataset.getName());
        assertEquals(testOutputDataset.getNamespace(), openLineageOutputDataset.getNamespace());
        DatasetFacet expectedFacet = testOutputDataset.getFacets().get("testOutputDatasetFacet");
        OpenLineage.DatasetFacet actualFacet =
                (OpenLineage.DatasetFacet) openLineageOutputDataset.getFacets().getAdditionalProperties().values().toArray()[0];
        assertEquals(expectedFacet.getProducer(), actualFacet.get_producer());
    }

    @Given("a valid Output Dataset object with a defined output facet")
    public void a_valid_output_dataset_object_with_a_defined_output_facet() {
        testOutputDatasetWithOutputFacet = new OutputDataset("test_output_dataset", createTestFacets("testOutputDatasetFacet", new TestDatasetFacet()), createTestFacets("testOutputDatasetOutputFacet", new TestOutputDatasetFacet()));
    }

    @When("the Output Dataset's function to build an OpenLineage object is called with a defined output facet")
    public void the_output_dataset_s_function_to_build_an_open_lineage_object_is_called_with_a_defined_output_facet() {
        openLineageOutputDatasetWithOutputFacet = testOutputDatasetWithOutputFacet.getOpenLineageDataset();
    }

    @Then("a valid OpenLineage Output Dataset will be created with its fields populated including an output facet")
    public void a_valid_open_lineage_output_dataset_will_be_created_with_its_fields_populated_including_an_output_facet() {
        assertNotNull(openLineageOutputDatasetWithOutputFacet);
        assertEquals(testOutputDatasetWithOutputFacet.getName(), openLineageOutputDatasetWithOutputFacet.getName());
        assertEquals(testOutputDatasetWithOutputFacet.getNamespace(), openLineageOutputDatasetWithOutputFacet.getNamespace());
        OutputDatasetFacet expectedFacet = testOutputDatasetWithOutputFacet.getOutputFacets().get("testOutputDatasetOutputFacet");
        OpenLineage.DatasetFacet actualFacet =
                (OpenLineage.DatasetFacet) openLineageOutputDatasetWithOutputFacet.getFacets().getAdditionalProperties().values().toArray()[0];
        assertEquals(expectedFacet.getProducer(), actualFacet.get_producer());
    }

    @Given("a valid Run Event object")
    public void a_valid_run_event_object() {
        testRunFacet = new TestRunFacet();
        testRun = new Run(UUID.randomUUID(), createTestFacets("runFacet", testRunFacet));

        testJobFacet = new TestJobFacet();
        testJob = new Job("test_job", createTestFacets("jobFacet", testJobFacet));

        DatasetFacet inputDatasetFacet = new TestDatasetFacet();
        testInputDataset = new InputDataset("test_input_dataset", createTestFacets("inputDatasetFacet", inputDatasetFacet));

        DatasetFacet outputDatasetFacet = new TestDatasetFacet();
        testOutputDataset = new OutputDataset("test_output_dataset", createTestFacets("outputDatasetFacet", outputDatasetFacet));

        testRunEvent = new RunEvent(testRun, testJob, "COMPLETE");
        testRunEvent.setInputs(Collections.singletonList(testInputDataset));
        testRunEvent.setOutputs(Collections.singletonList(testOutputDataset));
    }

    @When("the Run Event is converted to the OpenLineage format")
    public void the_run_event_is_converted_to_the_open_lineage_format() {
        openLineageRunEvent = testRunEvent.getOpenLineageRunEvent();
    }

    @Then("a valid OpenLineage Run Event will be created with its fields populated")
    public void a_valid_open_lineage_run_event_will_be_created_with_its_fields_populated() {
        assertNotNull(openLineageRunEvent);
        assertNotNull(openLineageRunEvent.getRun());
        assertEquals(testRun.getRunId(), openLineageRunEvent.getRun().getRunId());

        assertNotNull(openLineageRunEvent.getJob());
        assertEquals(testJob.getName(), openLineageRunEvent.getJob().getName());

        assertNotNull(openLineageRunEvent.getInputs());
        assertEquals(testInputDataset.getName(), openLineageRunEvent.getInputs().get(0).getName());
        assertEquals(testOutputDataset.getName(), openLineageRunEvent.getOutputs().get(0).getName());
    }

    @Given("a job named {string} for a RunEvent")
    public void a_job_named(String name) {
        testRunFacet = new TestRunFacet();
        testJobFacet = new TestJobFacet();
        Run testRun = new Run(UUID.randomUUID(), Collections.singletonMap("testFacet", testRunFacet));
        Job testJob = new Job(name, Collections.singletonMap("testJobFacet", testJobFacet));
        testRunEvent = new RunEvent(testRun, testJob, "START");
    }

    @Given("I have configured the property {string} with the value {string}")
    public void i_have_configured_the_property_with_the_value(String key, String value) {
        Properties properties = ConfigUtil.getInstance().properties;
        properties.setProperty(key, value);
    }

    @Then("the producer value of the event is set to {string}")
    public void the_producer_value_of_the_event_is_set_to(String producer) {
        assertEquals("The run event's producer value was not set correctly.", producer, openLineageRunEvent.getProducer().toString());
    }

    @Given("a valid Job object named {string}")
    public void a_valid_job_object_named(String name) {
        testJob = new Job(name, createTestFacets("testJobFacet", new TestJobFacet()));
    }

    @Then("the Job namespace value of the event is set to {string}")
    public void the_job_namespace_value_of_the_event_is_set_to(String namespace) {
        assertEquals(
                "The run event's job's namespace value was not set correctly.",
                namespace,
                openLineageRunEvent.getJob().getNamespace()
        );
    }

    @Given("the property {string} is not set")
    public void the_property_is_not_set(String property) {
        Properties properties = ConfigUtil.getInstance().properties;
        if (properties.stringPropertyNames().contains(property)) {
            properties.remove(property);
        }
    }

    @Given("a job named {string} has a default namespace {string}")
    public void a_job_named_has_a_default_namespace(String jobName, String defaultNamespace) {
        testJob = new Job(
                jobName,
                null,
                defaultNamespace
        );
    }

    @When("the Job is translated to an OpenLineage Job")
    public void the_job_is_translated_to_an_openlineage_job() {
        try {
            openLineageJob = testJob.getOpenLineageJob();
        } catch (Exception e) {
            caughtException = e;
        }

    }

    @Then("the Job namespace value is set to {string}")
    public void the_job_namespace_value_is_set_to(String namespace) {
        assertEquals(
                "The Open Lineage job's namespace value was not set correctly.",
                namespace,
                openLineageJob.getNamespace()
        );
    }

    @Then("an exception is raised")
    public void an_exception_is_raised() {
        assertTrue(
                "Caught exception was not NoSuchElementException.",
                caughtException instanceof NoSuchElementException
        );
    }

    @When("the Input Dataset is translated to an OpenLineage Dataset")
    public void the_input_dataset_is_translated_to_an_openlineage_dataset() {
        try {
            openLineageInputDataset = testInputDataset.getOpenLineageDataset();
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the namespace value of the Dataset is set to {string}")
    public void the_namespace_value_of_the_dataset_is_set_to(String namespace) {
        assertEquals(
                "The Open Lineage dataset's namespace value was not set correctly.",
                namespace,
                openLineageInputDataset.getNamespace()
        );
    }

    private Map<String, RunFacet> createTestFacets(String name, RunFacet facet) {
        Map<String, RunFacet> facets = new HashMap<String, RunFacet>();
        facets.put(name, facet);
        return facets;
    }

    private Map<String, JobFacet> createTestFacets(String name, JobFacet facet) {
        Map<String, JobFacet> facets = new HashMap<String, JobFacet>();
        facets.put(name, facet);
        return facets;
    }

    private Map<String, DatasetFacet> createTestFacets(String name, DatasetFacet facet) {
        Map<String, DatasetFacet> facets = new HashMap<String, DatasetFacet>();
        facets.put(name, facet);
        return facets;
    }

    private Map<String, InputDatasetFacet> createTestFacets(String name, InputDatasetFacet facet) {
        Map<String, InputDatasetFacet> facets = new HashMap<String, InputDatasetFacet>();
        facets.put(name, facet);
        return facets;
    }

    private Map<String, OutputDatasetFacet> createTestFacets(String name, OutputDatasetFacet facet) {
        Map<String, OutputDatasetFacet> facets = new HashMap<String, OutputDatasetFacet>();
        facets.put(name, facet);
        return facets;
    }

    private static class TestRunFacet extends RunFacet {
    }

    private static class TestJobFacet extends JobFacet {
    }

    private static class TestDatasetFacet extends DatasetFacet {
    }

    private static class TestInputDatasetFacet extends InputDatasetFacet {
    }

    private static class TestOutputDatasetFacet extends OutputDatasetFacet {
    }
}
