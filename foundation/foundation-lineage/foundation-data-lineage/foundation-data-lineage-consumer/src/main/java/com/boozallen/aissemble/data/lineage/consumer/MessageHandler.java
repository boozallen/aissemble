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

import com.fasterxml.jackson.core.type.TypeReference;
import io.openlineage.client.OpenLineage;
import io.openlineage.client.OpenLineageClientUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

/**
 * Parent utility class providing scaffolding for easier handling of received lineage events.  Specifically, this class
 * will handle asynchronous processing as well as intelligent ACK/NACK response for error handling, as configured in the
 * implementing application.
 */
public abstract class MessageHandler {
    private final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    /**
     * Defines desired behavior in the event of failures.
     * Default: NACK
     */
    @ConfigProperty(name = "datalineage.consumer.onFail", defaultValue = "NACK")
    FailureStrategy onFailStrategy;

    /**
     * Smallrye incoming channel name to bind to
     */
    public static final String CHANNEL_NAME = "data-lineage-in";

    /**
     * Helper function responsible for triggering processing and determining the response to a received event.
     * Executes the event processor, and determines the appropriate acknowledgement type.
     * By default, when executing with the NACK failure strategy, the acknowledgement occurs only after completion of
     * processing (PostProcessing acknowledgement).  With the DROP strategy, acknowledgement occurs immediately
     * (PreProcessing acknowledgement).
     *
     * @param event: The String-serialized Lineage Event Message for processing
     * @return: CompletionStage which, when completed, will result in the ack/nack of the message.
     */
    protected CompletionStage<Void> handleReceivedEvent(Message<String> event) {
        OpenLineage.RunEvent eventRepr = OpenLineageClientUtils.fromJson(event.getPayload(), new TypeReference<>() {});
        CompletableFuture<Void> future = CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> processRunEvent(event)),
                CompletableFuture.runAsync(() -> processRunEvent(event.getPayload())),
                CompletableFuture.runAsync(() -> processRunEvent(eventRepr))
        );
        switch (onFailStrategy) {
            case NACK:
                return handleNackStrategy(future, event);
            case DROP:
            default:
                return event.ack();
        }
    }

    /**
     * When a failure strategy of NACK is specified, determines the appropriate acknowledgement response.
     *
     * @param future The CompletableFuture representing processing of the received message
     * @param event The original incoming message to ack or nack.
     * @return CompletionStage representing either ack or nack, as appropriate
     */
    private CompletionStage<Void> handleNackStrategy(CompletableFuture<Void> future, Message<String> event) {
        try {
            future.join();
            return event.ack();
        } catch (CompletionException ce) {
            return event.nack(ce.getCause());
        }
    }

    /**
     * Overridable hook for processing the received event in the original received Message form.
     * Implementation is optional.  All implemented handlers execute concurrently.
     *
     * @param eventMessage The received Message
     */
    protected void processRunEvent(Message<String> eventMessage) {}

    /**
     * Overridable hook for processing the received event in the raw received JSON form.
     * Implementation is optional.  All implemented handlers execute concurrently.
     *
     * @param json The json representation of the received lineage event
     */
    protected void processRunEvent(String json) {}

    /**
     * Overridable hook for processing the received event after transformation to an OpenLineage RunEvent object.
     * Implementation is optional.  All implemented handlers execute concurrently.
     *
     * @param olEvent OpenLineage RunEvent representation of the received event
     */
    protected void processRunEvent(OpenLineage.RunEvent olEvent) {}

    protected Logger getLogger() { return this.logger; }

    public FailureStrategy getOnFailStrategy() { return this.onFailStrategy; }

    public void setOnFailStrategy(FailureStrategy strategy) {
        this.onFailStrategy = strategy;
    }
}
