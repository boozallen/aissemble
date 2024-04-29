package com.boozallen.aissemble.quarkus.context;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Messaging::Quarkus
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.spi.Extension;

import org.jboss.resteasy.microprofile.client.RestClientExtension;

import com.boozallen.aiops.core.cdi.CdiContext;

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
