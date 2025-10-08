package com.boozallen.aissemble.quarkus.context;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Messaging::Quarkus
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

import org.jboss.resteasy.microprofile.client.RestClientExtension;

import com.boozallen.aissemble.core.cdi.CdiContext;

import io.smallrye.faulttolerance.FaultToleranceExtension;
import io.smallrye.metrics.setup.MetricCdiInjectionExtension;

/**
 * {@link QuarkusCdiContext} adds the classes and extensions used by Quarkus
 * services.
 * 
 * @author Booz Allen Hamilton
 *
 */
public class QuarkusCdiContext implements CdiContext {

    @Override
    public List<Class<?>> getCdiClasses() {
        return null;
    }

    @Override
    public List<Extension> getExtensions() {
        final List<Extension> extensions = new ArrayList<>();
        extensions.add(new RestClientExtension());

        // Quarkus uses metrics, so this extension needs to be active
        extensions.add(new MetricCdiInjectionExtension());

        // Needed for fault tolerance
        extensions.add(new FaultToleranceExtension());
        return extensions;
    }

}
