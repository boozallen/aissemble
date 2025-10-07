package com.boozallen.aissemble.data.lineage.cdi;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Data Lineage Java
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

import com.boozallen.aissemble.data.lineage.transport.MessagingTransport;
import com.boozallen.aissemble.messaging.core.cdi.MessagingCdiContext;
import io.smallrye.reactive.messaging.providers.impl.ConnectorFactories;

import java.util.ArrayList;
import java.util.List;

/**
 * Adds necessary classes for Data Lineage event emission.
 * Note: Console Transport is added separately as an opt-in/opt-out.
 */
public class DataLineageCdiContext extends MessagingCdiContext {
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Class<?>> getCdiClasses() {

        List<Class<?>> transports = new ArrayList<>(super.getCdiClasses());

        transports.add(MessagingTransport.class);
        transports.add(ConnectorFactories.class);

        return transports;
    }
}
