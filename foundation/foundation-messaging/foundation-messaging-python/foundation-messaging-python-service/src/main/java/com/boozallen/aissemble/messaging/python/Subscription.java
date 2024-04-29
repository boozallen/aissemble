package com.boozallen.aissemble.messaging.python;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Messaging::Python::Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.messaging.python.transfer.Callback;
import com.boozallen.aissemble.messaging.python.transfer.MessageHandle;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Defines the subscription for a listener to consume message according to
 * the specified the ack strategy and the callback logic for processing the message.
 */
public class Subscription {
    private static final Logger logger = LoggerFactory.getLogger(Subscription.class);
    private final Callback callback;
    private final AckStrategy ackStrategy;

    public Subscription(Callback callback, AckStrategy ackStrategy) {
        this.callback = callback;
        this.ackStrategy = ackStrategy;
    }

    /**
     * Calls on the python callback object to process the given MessageHandle
     * @param messageHandle the MessageHandle to be processed
     */
    public CompletionStage<Void> notify(MessageHandle<String> messageHandle) {
        return (CompletionStage<Void>) this.callback.execute(messageHandle);
    }

    /**
     * Calls on the python callback object to process the microprofile message
     * @param message the microprofile message to be consumed
     */
    public CompletionStage<Void> notify(Message<String> message) {
        return this.notify(MessageHandle.createMessageHandle(message));
    }

    /**
     * Process the given message with subscription's acknowledgement strategy
     * @param message the message to be processed
     * @return CompletionStage
     */
    public CompletionStage<Void> processMessage(Message<String> message) {
        switch (ackStrategy) {
            case MANUAL:
                return processManualStrategy(message);
            case POSTPROCESSING:
                return processPostStrategy(message);
            default:
                throw new UnsupportedOperationException("Supported AckStrategy: AckStrategy.MANUAL and AckStrategy.POSTPROCESSING");

        }
    }

    /**
     * Process the given message with POSTPROCESSING acknowledgement strategy
     * @param message the message to be processed
     * @return CompletionStage
     */
    private CompletionStage<Void> processPostStrategy(Message<String> message) {
        try {
            notify(message);
        } catch (Throwable e) {
            logger.warn("Nack message: {}", message.getPayload());
            return message.nack(e);
        }
        // always ack message if there is no error
        return message.ack();
    }

    /**
     * Process the given message with MANUAL acknowledgement strategy
     * @param message the message to be processed
     * @return completionStage
     */
    private CompletionStage<Void> processManualStrategy(Message<String> message) {
        CompletionStage<Void> completionStage;
        try {
            // receive ack/nack response from the client
            completionStage = notify(message);
        } catch (Throwable e) {
            logger.warn("Nack message: {}", message.getPayload());
            return message.nack(e);
        }
        return completionStage == null ? CompletableFuture.completedFuture(null) : completionStage;
    }
}
