package com.boozallen.aissemble.kafka.context;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Messaging::Kafka
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.spi.Extension;

import com.boozallen.aiops.core.cdi.CdiContext;

import io.smallrye.reactive.messaging.kafka.KafkaCDIEvents;
import io.smallrye.reactive.messaging.kafka.KafkaConnector;

/**
 * {@link KafkaConnectorCdiContext} contains the classes needed by CDI when
 * using the {@link KafkaConnector}.
 * 
 * @author Booz Allen Hamilton
 *
 */
public class KafkaConnectorCdiContext implements CdiContext {

    @Override
    public List<Class<?>> getCdiClasses() {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(KafkaConnector.class);
        classes.add(KafkaCDIEvents.class);
        return classes;
    }

    @Override
    public List<Extension> getExtensions() {
        return null;
    }

}
