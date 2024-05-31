package com.boozallen.aissemble.alerting.core.cdi;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Alerting::Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.spi.Extension;

import com.boozallen.aissemble.alerting.core.AlertProducer;
import com.boozallen.aissemble.alerting.core.AlertProducerApi;
import com.boozallen.aissemble.alerting.core.consumer.AlertLogger;
import io.smallrye.reactive.messaging.providers.extension.ChannelProducer;
import io.smallrye.reactive.messaging.providers.impl.ConfiguredChannelFactory;
import io.smallrye.reactive.messaging.providers.impl.ConnectorFactories;
import io.smallrye.reactive.messaging.providers.connectors.ExecutionHolder;
import io.smallrye.reactive.messaging.providers.extension.HealthCenter;
import io.smallrye.reactive.messaging.providers.impl.InternalChannelRegistry;
import io.smallrye.reactive.messaging.providers.MediatorFactory;
import io.smallrye.reactive.messaging.providers.extension.MediatorManager;
import io.smallrye.reactive.messaging.providers.wiring.Wiring;
import io.smallrye.reactive.messaging.providers.connectors.WorkerPoolRegistry;
import com.boozallen.aissemble.core.cdi.CdiContext;

public class AlertingCdiContext implements CdiContext {

    @Override
    public List<Class<?>> getCdiClasses() {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(AlertProducer.class);
        classes.add(AlertLogger.class);
        classes.add(AlertProducerApi.class);
        classes.add(ChannelProducer.class);
        classes.add(ConfiguredChannelFactory.class);
        classes.add(ConnectorFactories.class);
        classes.add(ExecutionHolder.class);
        classes.add(HealthCenter.class);
        classes.add(InternalChannelRegistry.class);
        classes.add(MediatorFactory.class);
        classes.add(MediatorManager.class);
        classes.add(Wiring.class);
        classes.add(WorkerPoolRegistry.class); 
        return classes;
    }

    @Override
    public List<Extension> getExtensions() {
        return null;
    }

}
