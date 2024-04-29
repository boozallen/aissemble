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

import com.boozallen.aissemble.messaging.python.channel.impl.TopicEmitterImpl;
import com.boozallen.aissemble.messaging.python.channel.impl.TopicListenerImpl;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.MemberAttributeExtension;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.PackageDefinitionStrategy;
import net.bytebuddy.implementation.FixedValue;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * A Utility class for building TopicEmitters and TopicListeners with the microprofile configurations.
 */
public class ChannelFactory {

    private static final Logger logger = LoggerFactory.getLogger(ChannelFactory.class);
    private static final String OUTGOING_PROPERTY_PREFIX = "mp\\.messaging\\.outgoing\\.";
    private static final String INCOMING_PROPERTY_PREFIX = "mp\\.messaging\\.incoming\\.";
    private static final String PROPERTY_REGEX = "(.*?)\\.(.*)";
    private static final String CLASS_NAMESPACE = "com.boozallen.aissemble.messaging.python.channel.impl.%s";

    /**
     * Builds a map of the outgoing channels and topic names from the microprofile configurations.
     * @return A map of topic name -> channel name
     */
    public static HashMap<String, String> loadOutgoingChannels() {
        return loadChannels(OUTGOING_PROPERTY_PREFIX);
    }

    /**
     * Builds a map of the incoming channels and topic names from the microprofile configurations.
     * @return A map of topic name -> channel name
     */
    public static HashMap<String, String> loadIncomingChannels() {
        return loadChannels(INCOMING_PROPERTY_PREFIX);
    }

    /**
     * Builds a map of the channels and topic names with given channel property regex from the microprofile
     * configurations
     * @param channelRegex OUTGOING_PROPERTY_PREFIX or INCOMING_PROPERTY_PREFIX
     * @return A map of topic name -> channel name
     */
    private static HashMap<String, String> loadChannels(String channelRegex) {
        Config config = ConfigProvider.getConfig();
        HashMap<String, String> channels = new HashMap<>();
        for (String property : config.getPropertyNames()) {
            Pattern pattern = Pattern.compile(channelRegex + PROPERTY_REGEX);
            Matcher matcher = pattern.matcher(property);
            if (matcher.find()) {
                String channelName = matcher.group(1);
                String propertyType = matcher.group(2);
                // the client can specify the topic name using either topic (kafka) or address (amqp)
                if("topic".equals(propertyType) || "address".equals(propertyType)) {
                    String topicName = config.getConfigValue(property).getValue();
                    channels.put(topicName, channelName);
                } else {
                    String connector = config.getConfigValue(channelRegex.replace("\\", "") + channelName + ".connector").getValue();
                    String topicName = config.getConfigValue(channelRegex.replace("\\", "") + channelName + ".topic").getValue();
                    // get the amqp topic from the address property
                    if ("smallrye-amqp".equalsIgnoreCase(connector)) {
                        topicName = config.getConfigValue(channelRegex.replace("\\", "") + channelName + ".address").getValue();
                    }
                    // use channel as topic if no topic is specified by "topic" or "address"
                    if(topicName == null) {
                        channels.put(channelName, channelName);
                    }
                }
            }
        }
        return channels;
        //for each property that matches the pattern, see if it has a topic property
        // if it does, add that and the channel name to the map, otherwise add the channel name for both
        //keep a list of already added channels, and ignore further matches on that name
    }

    /**
     * Create emitter classes based on the configuration channels and topics
     * @return a collection of emitter classes
     */
    public List<Class<? extends TopicEmitterImpl>> getEmitters() {
        HashMap<String, String> channels = loadOutgoingChannels();
        List<Class<? extends TopicEmitterImpl>> emitters = new ArrayList<>();
        channels.forEach((topic, channel) -> emitters.add(createEmitterFor(channel, topic)));
        return emitters;
    }

    /**
     * Create the TopicEmitter class with the given channel and the topic
     * @param channelName the given channel for the TopicEmitter class
     * @param topicName the given topic for the TopicEmitter class
     * @return the TopicEmitter class
     */
    private Class<? extends TopicEmitterImpl> createEmitterFor(String channelName, String topicName) {
        // starts emitter class name with _ in case that topic is all numerical
        var className = String.format("_%sEmitter", topicName.replaceAll("[^a-zA-Z0-9]+", ""));
        logger.info("Creating {} for channel: {}, topic: {}.", className, channelName, topicName);
        return new ByteBuddy()
                .redefine(TopicEmitterImpl.class)
                .implement(TopicEmitter.class)
                .name(String.format(CLASS_NAMESPACE, className))
                .method(named("getTopic")).intercept(FixedValue.value(topicName))
                .visit(new MemberAttributeExtension.ForField().annotate(new Channel() {
                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return Channel.class;
                    }
                    @Override
                    public String value() {
                        return channelName;
                    }
                }).on(named("emitter")))
                .make()
                .load(TopicEmitterImpl.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER.with(PackageDefinitionStrategy.Trivial.INSTANCE))
                .getLoaded();
    }

    /**
     * Create listener classes based on the configuration channels and topics
     * @return a collection of emitter listener classes
     */
    public List<Class<? extends TopicListenerImpl>> getListeners() {
        HashMap<String, String> channels = loadIncomingChannels();
        List<Class<? extends TopicListenerImpl>> listeners = new ArrayList<>();
        channels.forEach((topic, channel) -> listeners.add(createListenerFor(channel, topic)));
        return listeners;
    }

    /**
     * Create the TopicListener class with the given channel and the topic
     * @param channelName the given channel for the TopicListener class to listen to
     * @param topicName the given topic for the TopicListener class to listen to
     * @return the Listener class
     */
    private Class<? extends TopicListenerImpl> createListenerFor(String channelName, String topicName) {
        // starts listener class name with _ in case that topic is all numerical
        var className = String.format("_%sListener", topicName.replaceAll("[^a-zA-Z0-9]+", ""));
        logger.info("Creating {} for channel: {}, topic: {}.", className, channelName, topicName);
        return new ByteBuddy()
                .redefine(TopicListenerImpl.class)
                .implement(TopicListener.class)
                .name(String.format(CLASS_NAMESPACE, className))
                .method(named("getTopic")).intercept(FixedValue.value(topicName))
                .visit(new MemberAttributeExtension.ForMethod().annotateMethod(new Incoming() {
                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return Incoming.class;
                    }
                    @Override
                    public String value() {
                        return channelName;
                    }
                }).on(named("consume")))
                .make()
                .load(TopicListenerImpl.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER.with(PackageDefinitionStrategy.Trivial.INSTANCE))
                .getLoaded();
    }

}
