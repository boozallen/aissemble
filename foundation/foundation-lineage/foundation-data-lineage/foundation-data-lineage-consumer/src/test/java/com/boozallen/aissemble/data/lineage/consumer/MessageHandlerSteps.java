package com.boozallen.aissemble.data.lineage.consumer;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Data Lineage Consumer Base
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.data.lineage.Job;
import com.boozallen.aiops.data.lineage.Run;
import com.boozallen.aiops.data.lineage.RunEvent;
import com.boozallen.aissemble.data.lineage.consumer.subclass.ExceptionalMessageReceiver;
import com.boozallen.aissemble.data.lineage.consumer.subclass.SuccessfulMessageReceiver;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.openlineage.client.OpenLineageClientUtils;
import org.eclipse.microprofile.reactive.messaging.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

public class MessageHandlerSteps {
    private Message<String> eventContent;
    private CompletionStage<Void> voidCompletionStage;
    private MessageHandler messageHandler;
    private Map<String, Object> context;

    private void detectAckType(Message<String> msg, Map<String, Object> context) {
        CompletionStage<Void> registerNack = CompletableFuture.supplyAsync(() -> { context.put("ack", AcknowledgementType.NACK); return Void.TYPE.cast(null); });
        this.eventContent = msg.withNack((exception) -> registerNack);
        Supplier<CompletionStage<Void>> registerAck = () -> CompletableFuture.supplyAsync(() -> { context.put("ack", AcknowledgementType.ACK); return Void.TYPE.cast(null);});
        this.eventContent = this.eventContent.withAck(registerAck);
    }

    @Before(value = "@detectAckType", order = 1000)
    public void setup_ack_detection() {
        this.detectAckType(this.eventContent, this.context);
    }

    @Before(value = "@exceptional")
    public void setup_exceptional_handler() {
        this.messageHandler = new ExceptionalMessageReceiver();
    }

    @Before(value = "@successful")
    public void setup_successful_handler() {
        this.messageHandler = new SuccessfulMessageReceiver();
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

    @Given("the failure strategy is configured to {}")
    public void failure_strategy_configured_to(String strat) {
        messageHandler.setOnFailStrategy(FailureStrategy.valueOf(strat));
    }

    @When("an exception occurs during message processing")
    public void exception_occurs_during_processing() {
        this.voidCompletionStage = messageHandler.handleReceivedEvent(eventContent);
    }

    @When("message processing occurs successfully")
    public void message_process_successfully() {
        this.voidCompletionStage = messageHandler.handleReceivedEvent(eventContent);
    }

    @Then("the message acknowledgement is {}")
    public void verify_ack_type(String ack) {
        try {
            voidCompletionStage.toCompletableFuture().get();
        } catch (Exception ignored) {}
        finally {
            assert (this.context.get("ack") == AcknowledgementType.valueOf(ack));
        }
    }
}
