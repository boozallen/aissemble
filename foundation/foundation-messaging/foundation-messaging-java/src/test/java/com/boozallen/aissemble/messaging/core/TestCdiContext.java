package com.boozallen.aissemble.messaging.core;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Messaging
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import javax.enterprise.inject.spi.Extension;
import java.util.ArrayList;
import java.util.List;
import com.boozallen.aiops.core.cdi.CdiContext;

public class TestCdiContext implements CdiContext {

    List<Class<?>> classes = new ArrayList<Class<?>>();

    @Override
    public List<Class<?>> getCdiClasses() {
        return classes;
    }

    @Override
    public List<Extension> getExtensions() {
        return null;
    }

    protected void addClass(Class<?> clazz) {
        classes.add(clazz);
    }
}
