package com.boozallen.aissemble.messaging.python.cdi;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Messaging::Python::Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.messaging.core.cdi.MessagingCdiContext;
import io.smallrye.reactive.messaging.amqp.AmqpConnector;
import io.smallrye.reactive.messaging.kafka.KafkaCDIEvents;
import io.smallrye.reactive.messaging.kafka.KafkaConnector;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.providers.impl.ConnectorFactories;

import java.util.ArrayList;
import java.util.List;

public class MessagingServiceCdiContext extends MessagingCdiContext {
    private List<Class<?>> moreClasses;

    public MessagingServiceCdiContext(List<Class<?>> classes) {
        if (classes != null) {
            this.moreClasses = classes;
        }
    }

    @Override
    public List<Class<?>> getCdiClasses() {
        List<Class<?>> beans = new ArrayList<>(super.getCdiClasses());

        beans.add(ConnectorFactories.class);
        beans.add(InMemoryConnector.class);
        // amqp connector
        beans.add(AmqpConnector.class);

        // kafka connector
        beans.add(KafkaConnector.class);
        beans.add(KafkaCDIEvents.class);

        if (moreClasses!= null && moreClasses.size() > 0) {
            beans.addAll(moreClasses);
        }

        return beans;
    }
}
