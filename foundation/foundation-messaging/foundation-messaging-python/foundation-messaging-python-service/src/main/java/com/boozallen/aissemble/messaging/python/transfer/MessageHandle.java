package com.boozallen.aissemble.messaging.python.transfer;

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

import org.eclipse.microprofile.reactive.messaging.Message;

import java.util.concurrent.CompletionStage;

/**
 * This defines the handles for the python client to process Java microprofile message
 * @param <T>
 */
public interface MessageHandle<T> {
    /**
     * Allows python class to retrieve the payload from java microprofile Message object
     * @return payload
     */
    T getPayload();

    /**
     * Allows python class to call the Microprofile message ack function
     * @return CompletionStage
     */
    CompletionStage<Void> ack();

    /**
     * Allows python class to call the Microprofile message nack function
     * @param reason the reason to nack the message
     * @return CompletionStage
     */
    CompletionStage<Void> nack(String reason);

    /**
     * creates an MessageHandle object with given java Microprofile Message object
     * @param message Java microprofile message
     * @param <T> payload
     * @return MessageHandle object
     */
    static <T> MessageHandle<T> createMessageHandle(Message<T> message) {
        return new MessageHandle<T>() {
            @Override
            public T getPayload() {
                return message.getPayload();
            }

            @Override
            public CompletionStage<Void> ack() {
                return message.ack();
            }

            @Override
            public CompletionStage<Void> nack(String reason) {
                return message.nack(new Throwable(reason));
            }
        };
    }
}
