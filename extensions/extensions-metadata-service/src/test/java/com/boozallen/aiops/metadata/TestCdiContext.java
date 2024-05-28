package com.boozallen.aiops.metadata;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Docker::AIOps Metadata Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.core.cdi.CdiContext;
import io.smallrye.reactive.messaging.providers.connectors.InMemoryConnector;

import javax.enterprise.inject.spi.Extension;
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
        return classes;
    }

    @Override
    public List<Extension> getExtensions() {
        return Collections.emptyList();
    }

}
