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

import com.boozallen.aissemble.messaging.python.Subscription;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Message;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.CompletionStage;

/**
 * The topic listener class that consumes the incoming message for a specific topic.
 */
@ApplicationScoped
public class TopicListenerImpl {

    private String topic;
    private Subscription subscription;

    /**
     * Get that topic that the listener listens to
     * @return topic of the listener
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Set listener's subscription
     * @param subscription the listener's subscription
     */
    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    /**
     * Get listener's subscription
     * @return subscription the listener's subscription
     */
    public Subscription getSubscription() {
        return subscription;
    }

    /**
     * Defines consuming messages from broker and handling futures.
     * Handles creation and returning of future independent of python callback object's
     * processing of the message. However, any message before subscription made is ignored
     * @param message the microprofile message received from the broker
     * @return future confirming consumption of message
     * @throws UnsupportedOperationException when there is no supported acknowledgement is registered
     */
    @Acknowledgment(Acknowledgment.Strategy.MANUAL)
    public CompletionStage<Void> consume(Message<String> message) throws UnsupportedOperationException {
        Subscription subscription = this.getSubscription();
        if (subscription != null) {
            return subscription.processMessage(message);
        }
        // ignore all the message before subscription is made
        return message.ack();
    }
}
