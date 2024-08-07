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

import com.boozallen.aissemble.core.cdi.CdiContainer;
import com.boozallen.aissemble.messaging.python.cdi.MessagingServiceCdiContext;
import com.boozallen.aissemble.messaging.python.channel.TopicEmitter;
import com.boozallen.aissemble.messaging.python.channel.TopicListener;
import com.boozallen.aissemble.messaging.python.channel.impl.TopicEmitterImpl;
import com.boozallen.aissemble.messaging.python.channel.impl.TopicListenerImpl;
import com.boozallen.aissemble.messaging.python.exception.TopicNotSupportedError;
import com.boozallen.aissemble.messaging.python.transfer.Callback;
import com.boozallen.aissemble.messaging.python.channel.ChannelFactory;

import javax.enterprise.inject.spi.CDI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Singleton messaging service housing relevant emitters and listeners that the messaging client leverages.
 */
public final class MessagingService {

    private static MessagingService INSTANCE;
    private final Map<String, TopicEmitter> emitters;
    private final Map<String, TopicListener> listeners;

    public synchronized static MessagingService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MessagingService();
        }
        return INSTANCE;
    }

    private MessagingService() {
        ChannelFactory factory = new ChannelFactory();
        List<Class<? extends TopicEmitterImpl>> emitterClasses = factory.getEmitters();
        List<Class<? extends TopicListenerImpl>> listenerClasses = factory.getListeners();

        // add the Emitter and Listener classes to the CDI beans
        CdiContainer.create(List.of(new MessagingServiceCdiContext(getEmitterAndListenerClazz(emitterClasses, listenerClasses))));

        // add in emitters
        emitters = new HashMap<>();
        emitterClasses.forEach((clazz) -> {
            TopicEmitter topicEmitter = (TopicEmitter) CDI.current().select(clazz).get();
            emitters.put(topicEmitter.getTopic(), topicEmitter);
        });

        // add in listeners
        listeners = new HashMap<>();
        listenerClasses.forEach((clazz) -> {
            TopicListener topicListener = (TopicListener) CDI.current().select(clazz).get();
            listeners.put(topicListener.getTopic(), topicListener);
        });
    }

    /**
     * Subscribes the appropriate TopicListener to topic provided. Any message to the given topic before
     * the subscription will be ignored
     * @param topic            the messaging topic to subscribe to
     * @param callback         defines the logic for processing the message
     * @param ackStrategyValue the integer representation of the ack strategy
     * @throws TopicNotSupportedError if the specified topic does not exist in the service
     */
    public void subscribe(String topic, Callback callback, int ackStrategyValue) throws TopicNotSupportedError {
        if (listeners.get(topic) == null) {
            throw new TopicNotSupportedError(topic);
        }
        AckStrategy ackStrategy = AckStrategy.valueOf(ackStrategyValue);
        TopicListener listener = listeners.get(topic);
        listener.setSubscription(new Subscription(callback, ackStrategy));
    }

    /**
     * Publishes to the broker the given message to the specified topic
     * @param topic   the messaging topic to publish to
     * @param message the string representation of the message
     * @return a future object to confirm emission of message to broker
     * @throws TopicNotSupportedError if the specified topic does not exist in the service
     */
    public Future<Void> publish(String topic, String message) throws TopicNotSupportedError {
        if (emitters.get(topic) == null) {
            throw new TopicNotSupportedError(topic);
        }
        return emitters.get(topic).emit(message);
    }

    /**
     * Confirms if the service has a listener that is subscribed to the given topic
     * @param topic the messaging topic
     * @return a boolean confirming if the listener of the given topic has a valid subscription
     * @throws TopicNotSupportedError if the specified topic does not exist in the service
     */
    public boolean isTopicSubscribed(String topic) throws TopicNotSupportedError {
        if (listeners.get(topic) == null) {
            throw new TopicNotSupportedError(topic);
        }
        return (listeners.get(topic).getSubscription() != null);
    }

    /**
     * Get the subscription for the given topic
     * @param topic the message topic
     * @return the subscription of the listener that can process the message for the given topic
     * @throws TopicNotSupportedError if the specified topic does not exist in the service
     */
    public Subscription getSubscription(String topic) throws TopicNotSupportedError {
        TopicListener listener = listeners.get(topic);
        if (listener == null) {
            throw new TopicNotSupportedError(topic);
        }
        return listener.getSubscription();
    }

    /**
     * Returns the service's TopicEmitters
     * @return TopicEmitters
     */
    public Map<String, TopicEmitter> getEmitters() {
        return emitters;
    }

    /**
     * Returns the service's TopicListeners
     * @return TopicListeners
     */
    public Map<String, TopicListener> getListeners() {
        return listeners;
    }

    private List<Class<?>> getEmitterAndListenerClazz(List<Class<? extends TopicEmitterImpl>> emitterClazzMap, List<Class<? extends TopicListenerImpl>> listenerClazzMap) {
        List<Class<?>> emittersAndListeners =  new ArrayList<>();
        emittersAndListeners.addAll(emitterClazzMap);
        emittersAndListeners.addAll(listenerClazzMap);
        return emittersAndListeners;
    }
}
