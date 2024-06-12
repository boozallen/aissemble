package com.boozallen.aissemble.data.lineage.cdi;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Data Lineage Java
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
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
