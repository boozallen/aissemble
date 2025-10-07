package com.boozallen.aissemble.datalineage.consumer;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Data Lineage::Http Consumer Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.boozallen.aissemble.data.lineage.consumer.MessageHandler;
import io.smallrye.common.constraint.NotNull;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.CDI;
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
