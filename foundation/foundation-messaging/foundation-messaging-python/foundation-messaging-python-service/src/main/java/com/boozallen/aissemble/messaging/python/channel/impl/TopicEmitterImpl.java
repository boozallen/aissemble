package com.boozallen.aissemble.messaging.python.channel.impl;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Messaging::Python::Service
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
import org.eclipse.microprofile.reactive.messaging.Emitter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
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
