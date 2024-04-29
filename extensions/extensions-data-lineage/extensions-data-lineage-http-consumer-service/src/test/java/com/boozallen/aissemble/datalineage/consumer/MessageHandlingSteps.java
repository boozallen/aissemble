package com.boozallen.aissemble.datalineage.consumer;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Data Lineage::Http Consumer Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.data.lineage.Job;
import com.boozallen.aiops.data.lineage.Run;
import com.boozallen.aiops.data.lineage.RunEvent;
import com.boozallen.aissemble.data.lineage.consumer.AcknowledgementType;
import com.boozallen.aissemble.data.lineage.consumer.FailureStrategy;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.openlineage.client.OpenLineageClientUtils;
import org.eclipse.microprofile.reactive.messaging.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ApplicationScoped
public class MessageHandlingSteps {
    static final int WMS_PORT = 15100;
    private CompletionStage<Void> voidCompletionStage;
    private WireMockServer wms = null;
    private Message<String> eventContent;
    private Map<String, Object> context;

    @Inject
    LineageMessageHandler messageHandler;

    private final Logger logger = LoggerFactory.getLogger(MessageHandlingSteps.class);

    /**
     * Replaces the default ack/nack functions with logic to mark their outflow in the context map for future reference.
     */
    private void detectAckType(Message<String> msg, Map<String, Object> context) {
        CompletionStage<Void> registerNack = CompletableFuture.supplyAsync(() -> { context.put("ack", AcknowledgementType.NACK); return Void.TYPE.cast(null); });
        this.eventContent = msg.withNack((exception) -> {
            context.put("exception", exception);
            context.put("exceptionMessage", exception.getClass().getName() + ": " + exception.getMessage());
            return registerNack;
        });
        Supplier<CompletionStage<Void>> registerAck = () -> CompletableFuture.supplyAsync(() -> { context.put("ack", AcknowledgementType.ACK); return Void.TYPE.cast(null);});
        this.eventContent = this.eventContent.withAck(registerAck);
    }

    @Before(value = "@detectAckType", order = 1000)
    public void setup_ack_detection() {
        this.detectAckType(this.eventContent, this.context);
    }

    /**
     * Helper function for emitting an event to the message receiver
     */
    @Channel(LineageMessageHandler.CHANNEL_NAME)
    @OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = 20)
    @Inject
    Emitter<String> stringEmitter;
    private void sendEventMessage() {
        voidCompletionStage = stringEmitter.send(eventContent.getPayload());
    }

    /**
     * If the endpoint needs to be available for a test, ensures that the endpoint is reachable.
     */
    @Before("@endpointExists")
    public void http_setup() {
        if (wms == null) {
            wms = new WireMockServer(wireMockConfig().port(WMS_PORT));
            WireMock.configureFor("localhost", WMS_PORT);
        }
        wms.stubFor(post("/endpoint")
                .withHeader("Content-Type", containing("application/json"))
                .willReturn(ok()
                        .withHeader("Content-Type", "text/plain")
                        .withBody("<response>SUCCESS</response>")));
        if (!wms.isRunning()) {
            wms.start();
        }
    }

    /**
     * If the endpoint should not be reachable for a test, ensures that the endpoint is unavailable.
     */
    @Before("@endpointNotExists")
    public void http_disable() {
        if (wms != null && wms.isRunning()) {
            wms.stop();
        }
    }

    /**
     * Resets content between scenarios
     */
    @Before(order = 100)
    public void reset_test_context() {
        context = new HashMap<>();

        voidCompletionStage = null;

        Job job = new Job("myJob", "myNamespace");
        Run run = new Run(UUID.randomUUID());
        RunEvent event = new RunEvent(run, job, "START");
        eventContent = Message.of(OpenLineageClientUtils.toJson(event.getOpenLineageRunEvent()));
    }

    @When("a message is sent to the incoming channel")
    public void send_run_event_message() {
        sendEventMessage();
    }

    @Then("the process completes successfully")
    public void process_completes_successfully() {
        voidCompletionStage.whenComplete((nothing, exception) -> {
            assertNull(exception, "Exception thrown during processing");
        }).exceptionally((exception) -> Void.TYPE.cast(null)).toCompletableFuture().join();
    }

    @Then("the message payload is sent to the HTTP endpoint")
    public void payload_received_by_http() {
        //wms.findAll(postRequestedFor()).getRequests().forEach((req) -> logger.info(req.toString()));
        this.wms.verify(postRequestedFor(urlPathEqualTo("/endpoint")));
    }

    @Given("the HTTP endpoint is unavailable")
    public void http_endpoint_unavailable() {}

    @Given("the HTTP endpoint is available")
    public void http_endpoint_available() {}

    @Given("the failure strategy is configured to {}")
    public void failure_strategy_configured_to(String strat) {
        messageHandler.setOnFailStrategy(FailureStrategy.valueOf(strat));
    }

    @When("a message attempts processing")
    public void message_attempts_processing() {
        this.voidCompletionStage = messageHandler.receiveLineageEvent(this.eventContent);
    }

    @Then("the message acknowledgement is {}")
    public void verify_ack_type(String ack) {
        try {
            voidCompletionStage.toCompletableFuture().get();
        } catch (Exception ignored) {}
        finally {
            assertEquals(AcknowledgementType.valueOf(ack), this.context.get("ack"),
                    "Incorrect ack type received (exception[" + this.context.get("exceptionMessage") +"])");
        }
    }
}
