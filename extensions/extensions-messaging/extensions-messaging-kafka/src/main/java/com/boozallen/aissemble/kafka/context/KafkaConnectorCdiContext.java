package com.boozallen.aissemble.kafka.context;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Messaging::Kafka
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

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.inject.spi.Extension;

import com.boozallen.aissemble.core.cdi.CdiContext;

import io.smallrye.reactive.messaging.kafka.KafkaCDIEvents;
import io.smallrye.reactive.messaging.kafka.KafkaConnector;
import io.smallrye.reactive.messaging.kafka.commit.KafkaThrottledLatestProcessedCommit;
import io.smallrye.reactive.messaging.kafka.fault.KafkaFailStop;

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
        classes.add(KafkaThrottledLatestProcessedCommit.Factory.class);
        classes.add(KafkaFailStop.Factory.class);
        return classes;
    }

    @Override
    public List<Extension> getExtensions() {
        return null;
    }

}
