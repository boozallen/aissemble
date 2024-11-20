package com.boozallen.drift.detection.cdi;

/*-
 * #%L
 * Drift Detection::Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.core.cdi.CdiContext;
import com.boozallen.drift.detection.consumer.TestConsumer;
import jakarta.enterprise.inject.spi.Extension;

import java.util.ArrayList;
import java.util.List;

public class TestCdiContext implements CdiContext {

    @Override
    public List<Class<?>> getCdiClasses() {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(TestConsumer.class);
        return classes;
    }

    @Override
    public List<Extension> getExtensions() {
        return List.of();
    }

}
