package com.boozallen.drift.detection;

/*-
 * #%L
 * Drift Detection::Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.alerting.core.cdi.AlertingCdiContext;
import com.boozallen.aissemble.core.cdi.CdiContainer;
import com.boozallen.drift.detection.cdi.TestCdiContext;
import com.boozallen.drift.detection.consumer.TestConsumer;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.jboss.weld.environment.se.WeldContainer;

import java.util.List;

public class CommonCdiSteps {

    private WeldContainer container;

    @Before("@cdi")
    public void setUp() {
        container = CdiContainer.create(List.of(new TestCdiContext(), new AlertingCdiContext()));
    }

    @After("@cdi")
    public void tearDown() {
        if (container != null) {
            container.close();
            container = null;
        }
        TestConsumer.getAllAlerts().clear();
    }

}
