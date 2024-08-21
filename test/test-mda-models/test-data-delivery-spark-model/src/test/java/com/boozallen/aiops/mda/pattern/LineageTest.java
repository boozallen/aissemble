package com.boozallen.aiops.mda.pattern;

/*-
 * #%L
 * aiSSEMBLE::Test::MDA::Data Delivery Spark
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;

import java.util.List;

import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.spi.CDI;

import org.eclipse.microprofile.reactive.messaging.Message;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySink;
import com.boozallen.aiops.mda.pattern.pipeline.PipelineBase;
import com.boozallen.aiops.mda.pattern.Ingest;
import com.boozallen.aissemble.common.Constants;

import org.apache.hadoop.shaded.com.google.gson.JsonObject;
import org.apache.hadoop.shaded.com.google.gson.JsonParser;
/**
 * Implementation steps for lineage.feature
 */
public class LineageTest {

    private final String PIPELINE_NAME = "SparkJavaDataDeliveryPatterns";
    private InMemoryConnector connector;
    private Ingest ingest;
    private String stepName;

    @Before("@data-lineage")
    public void setUp() {
        // test objects to initialize before each scenario
        SparkTestHarness.getSparkSession();
        connector = CDI.current().select(InMemoryConnector.class, new Any.Literal()).get();
    }

    @After("@data-lineage")
    public void tearDown() {
        // test objects to clear after each scenario
        ingest = null;
        stepName = null;
        connector = null;
    }
    
    @Given("the pipeline has the step name Ingest")
    public void the_pipeline_has_the_step_name_ingest() {
        stepName = "Ingest";
        ingest = CDI.current().select(Ingest.class, new Any.Literal()).get();;
    }

    @Given("a pipeline run is created")
    public void a_pipeline_run_is_created() {
        PipelineBase.getInstance().recordPipelineLineageStartEvent();
    }
    
    @When("the step lineage run event is created")
    public void step_lineage_run_event_is_created() {
        // event is created when step is executed
        ingest.executeStep();
    }

    @Then("the step lineage run event's parent run is the pipeline run")
    public void the_step_lineage_run_event_parent_run_is_the_pipeline_run() {
        List<Message<Object>> lineageMessages = getPublishedMessages(Constants.DATA_LINEAGE_CHANNEL_NAME);
        JsonObject stepLineageEvent = findLineageEvent(PIPELINE_NAME + "." + stepName, lineageMessages);
        JsonObject pipelineLineageEvent = findLineageEvent(PIPELINE_NAME, lineageMessages);

        assertEquals(
                stepLineageEvent.getAsJsonObject("run")
                        .getAsJsonObject("facets")
                        .getAsJsonObject("parent")
                        .getAsJsonObject("run")
                        .get("runId").getAsString(),
                pipelineLineageEvent
                        .getAsJsonObject("run")
                        .get("runId").getAsString());
    }

    /**
     * Use this method, along with the configureMessagingChannels() method in {@link SparkTestHarness} to handle messaging in your
     * tests without having to stand up your messaging broker (e.g., Kafka) or use sleep commands.
     */
    @SuppressWarnings("unchecked")
    private List<Message<Object>> getPublishedMessages(String outgoingChannel) {
        // await the results as timing can vary - use this pattern and not sleep, which will add a lot of unnecessary
        // time to your builds:
        InMemorySink<Object> sink = connector.sink(outgoingChannel);
        
        await().<List<? extends Message<Object>>> until(sink::received, t -> t.size() >= 1);
        // return the messages that were published to the given outgoing channel
        return (List<Message<Object>>) sink.received();
    }

    private JsonObject findLineageEvent(String eventJobName, List<Message<Object>> lineageMessages) {
        for (Message<Object> data :lineageMessages) {
            JsonObject event = JsonParser.parseString(data.getPayload().toString()).getAsJsonObject();
            String jobName = event.getAsJsonObject("job").get("name").getAsString();
            if (jobName.equals(eventJobName)) {
                return event;
            }
        }
        return null;
    }
}
