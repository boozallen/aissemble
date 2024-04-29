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

import com.boozallen.aissemble.data.lineage.consumer.MessageHandler;
import io.smallrye.common.constraint.NotNull;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletionStage;

/**
 * Entrypoint for messages to be forwarded on to the HTTP endpoint.  Accepts incoming messages, triggers processing,
 * and responds as appropriate.
 */

@ApplicationScoped
public class LineageMessageHandler extends MessageHandler {
    /**
     * Determines whether to include stack traces in error logging.
     * Defaults to false as default kafka behavior in NACKs is to re-print the full stack trace.
     */
    @ConfigProperty(name = "datalineage.consumer.showStackTrace", defaultValue = "false")
    boolean showStackTrace;

    /**
     *
     * @param event: The received String-serialized Lineage Event Message
     * @return: CompletionStage representing the final asynchronous ack/nack of the message
     */
    @Acknowledgment(Acknowledgment.Strategy.MANUAL)
    @Incoming(CHANNEL_NAME)
    public CompletionStage<Void> receiveLineageEvent(Message<String> event) {
        getLogger().debug("Received event: " + event.getPayload());
        return handleReceivedEvent(event);
    }

    /**
     * Helper function to conduct our business logic on the received message.
     * Triggers submission of the message payload to the HTTP endpoint.  In the event of a failure, provides debugging
     * information.
     * @param payload: A String payload which shall be submitted to the HTTP endpoint.
     * @return: The failure exception, if applicable.
     */
    @Override
    protected void processRunEvent(@NotNull String payload) {
        HttpProducer producer = CDI.current().select(HttpProducer.class).get();
        try {
            getLogger().debug("HTTP POST Response: " + producer.postEventHttp(payload).toCompletableFuture().join());
        } catch(Exception exception) {
            getLogger().debug("HTTP POST failed for event: " + payload);
            getLogger().debug("Will proceed with failure strategy: " + getOnFailStrategy().name());
            getLogger().error("Exception message received: " + exception.getMessage());

            if (showStackTrace)
                getLogger().error(String.join("\r\n\t", Arrays.stream(exception.getStackTrace()).map(Objects::toString).toArray(String[]::new)));

            throw exception;
        }
    }
}
