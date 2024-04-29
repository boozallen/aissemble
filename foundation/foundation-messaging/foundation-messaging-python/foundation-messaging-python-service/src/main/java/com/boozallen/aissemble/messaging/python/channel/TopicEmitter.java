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

import java.util.concurrent.Future;

/**
 * The interface that defines the functions for topic emitter to emit a message to a specific topic
 */
public interface TopicEmitter {

    /**
     * Emit the given message to the set topic
     * @param message the message to emit
     * @return Future a future object to confirm emission of message to broker
     */
    Future<Void> emit(String message);

    /**
     * Get the topic of the topic emitter class
     * @return topic
     */
    String getTopic();
}
