package com.boozallen.aissemble.messaging.python.channel.impl;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Messaging::Python::Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */
import org.eclipse.microprofile.reactive.messaging.Emitter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Future;

/**
 * A Wrapper for the SmallRye Emitter class that allows the Emitter's Channel to be customized at runtime
 */
@ApplicationScoped
public class TopicEmitterImpl {

    private String topic;

    @Inject
    private Emitter<String> emitter;

    /**
     * Get the topic of the emitter
     * @return topic of the emitter
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Emit the given message with the injected emitter class
     * @param message the message to be emitted
     * @return Future a future object to confirm emission of message to broker
     */
    public Future<Void> emit(String message) {
        if (emitter.hasRequests()) {
            CompletionStage<Void> acked = emitter.send(message);
            return acked.toCompletableFuture();
        } else {
            throw new RuntimeException();
        }
    }
}
