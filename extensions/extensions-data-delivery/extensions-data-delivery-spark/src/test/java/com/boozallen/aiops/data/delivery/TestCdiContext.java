package com.boozallen.aiops.data.delivery;

/*-
 * #%L
 * AIOps Foundation::AIOps Data Delivery::Spark
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
import com.boozallen.aiops.data.delivery.messaging.PipelineMessagingConfig;
import com.boozallen.aiops.data.delivery.messaging.pipeline.steps.InOnlyTestStep;
import com.boozallen.aiops.data.delivery.messaging.pipeline.steps.InOutTestStep;
import com.boozallen.aiops.data.delivery.messaging.pipeline.steps.NoChannelTestStep;
import com.boozallen.aiops.data.delivery.messaging.pipeline.steps.OutOnlyTestStep;
import com.boozallen.aiops.data.delivery.messaging.pipeline.steps.OverrideTestStep;
import com.boozallen.aissemble.alerting.core.cdi.AlertingCdiContext;

import io.smallrye.config.inject.ConfigExtension;
import io.smallrye.reactive.messaging.providers.MediatorFactory;
import io.smallrye.reactive.messaging.providers.connectors.ExecutionHolder;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.providers.connectors.WorkerPoolRegistry;
import io.smallrye.reactive.messaging.providers.extension.ChannelProducer;
import io.smallrye.reactive.messaging.providers.extension.HealthCenter;
import io.smallrye.reactive.messaging.providers.extension.MediatorManager;
import io.smallrye.reactive.messaging.providers.extension.ReactiveMessagingExtension;
import io.smallrye.reactive.messaging.providers.impl.ConfiguredChannelFactory;
import io.smallrye.reactive.messaging.providers.impl.ConnectorFactories;
import io.smallrye.reactive.messaging.providers.impl.InternalChannelRegistry;
import io.smallrye.reactive.messaging.providers.wiring.Wiring;

import jakarta.enterprise.inject.spi.Extension;
import java.util.ArrayList;
import java.util.List;

public class TestCdiContext implements CdiContext {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Class<?>> getCdiClasses() {
        List<Class<?>> customBeans = new ArrayList<>();

        customBeans.add(InMemoryConnector.class);
        customBeans.add(InOutTestStep.class);
        customBeans.add(InOnlyTestStep.class);
        customBeans.add(OutOnlyTestStep.class);
        customBeans.add(NoChannelTestStep.class);
        customBeans.add(OverrideTestStep.class);

        customBeans.add(PipelineMessagingConfig.class);
        customBeans.add(MediatorFactory.class);
        customBeans.add(MediatorManager.class);
        customBeans.add(InternalChannelRegistry.class);
        customBeans.add(ConnectorFactories.class);
        customBeans.add(ConfiguredChannelFactory.class);
        customBeans.add(ChannelProducer.class);
        customBeans.add(ExecutionHolder.class);
        customBeans.add(HealthCenter.class);
        customBeans.add(WorkerPoolRegistry.class);
        customBeans.add(Wiring.class);

        AlertingCdiContext alertingContext = new AlertingCdiContext();
        List<Class<?>> alertingContextClasses = alertingContext.getCdiClasses();
        customBeans.addAll(alertingContextClasses);

        return customBeans;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Extension> getExtensions() {
        List<Extension> extensions = new ArrayList<>();

        extensions.add(new ConfigExtension());
        extensions.add(new ReactiveMessagingExtension());

        return extensions;
    }

}
