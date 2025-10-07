package com.boozallen.aissemble.messaging.python.channel;

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
