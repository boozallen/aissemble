package com.boozallen.aissemble.messaging.python.channel;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Messaging::Python::Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.messaging.python.Subscription;
import org.eclipse.microprofile.reactive.messaging.Message;

import java.util.concurrent.CompletionStage;

/**
 * The interface that defines the functions for a topic listener class to consume a message from a specific topic
 */
public interface TopicListener {

    /**
     * Get topic of the listener class
     * @return the topic of the listener that listens to
     */
    String getTopic();

    /**
     * Set the subscription of the topic listener
     * @param subscription the given subscription
     */
    void setSubscription(Subscription subscription);

    /**
     * Get the subscription of the topic listener
     * @return subscription
     */
    Subscription getSubscription();

    /**
     * Consume the given message from the subscribed topic
     * @param message the message to be consumed
     * @return CompletionStage
     * @throws UnsupportedOperationException if the specified topic does not exist in the service
     */
    CompletionStage<Void> consume(Message<String> message) throws UnsupportedOperationException;
}
