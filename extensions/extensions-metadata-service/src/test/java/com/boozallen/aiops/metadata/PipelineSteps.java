package com.boozallen.aiops.metadata;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Docker::AIOps Metadata Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.core.metadata.MetadataAPI;
import com.boozallen.aissemble.core.metadata.MetadataModel;
import com.boozallen.aiops.metadata.hive.HiveMetadataAPIService;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;


/**
 * Implementation steps for pipeline.feature.
 * <p>
 * GENERATED STUB CODE - PLEASE ***DO*** MODIFY
 * <p>
 * Originally generated from: templates/cucumber.pipeline.steps.java.vm
 */
@ApplicationScoped
public class PipelineSteps {

    MetadataAPI metadataAPI;
    private MetadataService metadataService;
    private int return_status;

    @Before("@metadata")
    public void setUp() {
        // test objects to initialize before each scenario
        metadataAPI = new HiveMetadataAPIService();
        metadataService = new MetadataService();
        metadataService.metadataAPI = metadataAPI;
    }

    @After("@metadata")
    public void tearDown() {
        // test objects to clear after each scenario
    }

    @Given("a populated metadata table")
    public void a_populated_metadata_table() {
        MetadataModel metadataModel = new MetadataModel("resource", "subject", "action");
        metadataAPI.createMetadata(metadataModel);
    }

    @When("the getMetadata endpoint is called")
    public void the_getMetadata_endpoint_is_called() {
        Response response = metadataService.getMetadata();
        return_status = response.getStatus();
    }

    @Then("return a 200 status code")
    public void return_a_200_code() {
        assertEquals(200, return_status);
    }
}
