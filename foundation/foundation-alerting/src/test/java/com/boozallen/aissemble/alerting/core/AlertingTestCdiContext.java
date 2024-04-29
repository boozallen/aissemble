package com.boozallen.aissemble.alerting.core;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Alerting
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;
import com.boozallen.aissemble.alerting.core.cdi.AlertingCdiContext;

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
import io.smallrye.reactive.messaging.providers.impl.ConnectorFactories;
import io.smallrye.reactive.messaging.providers.impl.InternalChannelRegistry;
import io.smallrye.reactive.messaging.providers.wiring.Wiring;

public class AlertingTestCdiContext extends AlertingCdiContext {

    List<Class<?>> classes = new ArrayList<Class<?>>();

    @Override
    public List<Class<?>> getCdiClasses() {
        List<Class<?>> classes = super.getCdiClasses();
        classes.add(AlertCounter.class);
        classes.add(ChannelProducer.class);
        classes.add(MediatorManager.class);
        classes.add(HealthCenter.class);
        classes.add(InternalChannelRegistry.class);
        classes.add(WorkerPoolRegistry.class);
        classes.add(MediatorFactory.class);
        classes.add(Wiring.class);

        return classes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Extension> getExtensions() {
        List<Extension> extensions = new ArrayList<Extension>();
        extensions.add(new ConfigExtension());
        extensions.add(new ReactiveMessagingExtension());

        return extensions;
    }
}
