package com.boozallen.aissemble.messaging.core.cdi;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Messaging::Messaging
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

import com.boozallen.aissemble.core.cdi.CdiContext;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.inject.spi.Extension;

import io.smallrye.config.inject.ConfigExtension;
import io.smallrye.reactive.messaging.providers.MediatorFactory;
import io.smallrye.reactive.messaging.providers.connectors.ExecutionHolder;
import io.smallrye.reactive.messaging.providers.connectors.WorkerPoolRegistry;
import io.smallrye.reactive.messaging.providers.extension.ChannelProducer;
import io.smallrye.reactive.messaging.providers.extension.EmitterFactoryImpl;
import io.smallrye.reactive.messaging.providers.extension.HealthCenter;
import io.smallrye.reactive.messaging.providers.extension.MediatorManager;
import io.smallrye.reactive.messaging.providers.extension.ReactiveMessagingExtension;
import io.smallrye.reactive.messaging.providers.impl.ConfiguredChannelFactory;
import io.smallrye.reactive.messaging.providers.impl.InternalChannelRegistry;
import io.smallrye.reactive.messaging.providers.wiring.Wiring;
import io.smallrye.reactive.messaging.providers.impl.ConnectorFactories;

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
        List<Class<?>> classes = new ArrayList<>();
        classes.add(MediatorFactory.class);
        classes.add(MediatorManager.class);
        classes.add(InternalChannelRegistry.class);
        classes.add(ConfiguredChannelFactory.class);
        classes.add(EmitterFactoryImpl.class);
        classes.add(ChannelProducer.class);
        classes.add(ExecutionHolder.class);
        classes.add(HealthCenter.class);
        classes.add(WorkerPoolRegistry.class);
        classes.add(Wiring.class);
        classes.add(ConnectorFactories.class);
        return classes;
    }

    @Override
    public List<Extension> getExtensions() {
        List<Extension> extensions = new ArrayList<>();
        extensions.add(new ConfigExtension());
        extensions.add(new ReactiveMessagingExtension());
        return extensions;
    }
}
