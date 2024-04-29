package com.boozallen.aissemble.messaging.python.transfer;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Messaging::Python::Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
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
