package com.boozallen.aiops.data.lineage.cdi;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Data Lineage Java
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */
import com.boozallen.aissemble.messaging.core.cdi.MessagingCdiContext;
import io.smallrye.reactive.messaging.providers.connectors.InMemoryConnector;
import java.util.ArrayList;
import java.util.List;

public class TestCdiContext extends MessagingCdiContext {
    @Override
    public List<Class<?>> getCdiClasses() {
        List<Class<?>> beans = new ArrayList<>(super.getCdiClasses());

        beans.add(InMemoryConnector.class);

        return beans;
    }
}
