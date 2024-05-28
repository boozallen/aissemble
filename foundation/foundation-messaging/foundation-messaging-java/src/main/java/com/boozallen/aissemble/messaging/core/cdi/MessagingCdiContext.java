package com.boozallen.aissemble.messaging.core.cdi;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Messaging::Messaging
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.core.cdi.CdiContext;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.spi.Extension;

import io.smallrye.config.inject.ConfigExtension;
import io.smallrye.reactive.messaging.providers.MediatorFactory;
import io.smallrye.reactive.messaging.providers.connectors.ExecutionHolder;
import io.smallrye.reactive.messaging.providers.connectors.WorkerPoolRegistry;
import io.smallrye.reactive.messaging.providers.extension.ChannelProducer;
import io.smallrye.reactive.messaging.providers.extension.HealthCenter;
import io.smallrye.reactive.messaging.providers.extension.MediatorManager;
import io.smallrye.reactive.messaging.providers.extension.ReactiveMessagingExtension;
import io.smallrye.reactive.messaging.providers.impl.ConfiguredChannelFactory;
import io.smallrye.reactive.messaging.providers.impl.InternalChannelRegistry;
import io.smallrye.reactive.messaging.providers.wiring.Wiring;

/**
 * {@link MessagingCdiContext} class provides the classes and extensions needed
 * for Smallrye Reactive Messaging.
 * 
 * @author Booz Allen Hamilton
 *
 */
public class MessagingCdiContext implements CdiContext {

    @Override
    public List<Class<?>> getCdiClasses() {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        classes.add(MediatorFactory.class);
        classes.add(MediatorManager.class);
        classes.add(InternalChannelRegistry.class);
        classes.add(ConfiguredChannelFactory.class);
        classes.add(ChannelProducer.class);
        classes.add(ExecutionHolder.class);
        classes.add(HealthCenter.class);
        classes.add(WorkerPoolRegistry.class);
        classes.add(Wiring.class);
        return classes;
    }

    @Override
    public List<Extension> getExtensions() {
        List<Extension> extensions = new ArrayList<Extension>();
        extensions.add(new ConfigExtension());
        extensions.add(new ReactiveMessagingExtension());
        return extensions;
    }
}
