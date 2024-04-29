package com.boozallen.aissemble.messaging.python.exception;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Messaging::Python::Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

public class TopicNotSupportedError extends Exception {

    private final String topic;

    public TopicNotSupportedError(String topicName) {
        super("Could not find a topic to subscribe to named " + topicName);
        topic = topicName;
    }
}
