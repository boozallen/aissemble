package com.boozallen.aissemble.messaging.python.cdi;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Messaging::Python::Service
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
