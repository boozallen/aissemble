package com.boozallen.aiops.metadata;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Docker::AIOps Metadata Service
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
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.providers.connectors.ExecutionHolder;

import jakarta.enterprise.inject.spi.Extension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestCdiContext implements CdiContext {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Class<?>> getCdiClasses() {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(InMemoryConnector.class);
        classes.add(ExecutionHolder.class);
        return classes;
    }

    @Override
    public List<Extension> getExtensions() {
        return Collections.emptyList();
    }

}
