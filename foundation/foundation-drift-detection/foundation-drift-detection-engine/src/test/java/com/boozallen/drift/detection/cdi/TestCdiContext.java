package com.boozallen.drift.detection.cdi;

/*-
 * #%L
 * Drift Detection::Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.core.cdi.CdiContext;
import com.boozallen.aissemble.alerting.core.AlertProducer;
import com.boozallen.aissemble.alerting.slack.SlackClient;
import com.boozallen.drift.detection.consumer.TestConsumer;
import io.smallrye.config.inject.ConfigExtension;
import io.smallrye.reactive.messaging.providers.MediatorFactory;
import io.smallrye.reactive.messaging.providers.connectors.WorkerPoolRegistry;
import io.smallrye.reactive.messaging.providers.extension.ChannelProducer;
import io.smallrye.reactive.messaging.providers.extension.HealthCenter;
import io.smallrye.reactive.messaging.providers.extension.MediatorManager;
import io.smallrye.reactive.messaging.providers.extension.ReactiveMessagingExtension;
import io.smallrye.reactive.messaging.providers.impl.InternalChannelRegistry;
import io.smallrye.reactive.messaging.providers.wiring.Wiring;

import javax.enterprise.inject.spi.Extension;
import java.util.ArrayList;
import java.util.List;

public class TestCdiContext  implements CdiContext {

    @Override
    public List<Class<?>> getCdiClasses() {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        classes.add(TestConsumer.class);
        classes.add(SlackClient.class);
        classes.add(AlertProducer.class);


        classes.add(ChannelProducer.class);
        classes.add(MediatorManager.class);
        classes.add(HealthCenter.class);
        classes.add(InternalChannelRegistry.class);
        classes.add(WorkerPoolRegistry.class);
        classes.add(MediatorFactory.class);
        classes.add(Wiring.class);

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
