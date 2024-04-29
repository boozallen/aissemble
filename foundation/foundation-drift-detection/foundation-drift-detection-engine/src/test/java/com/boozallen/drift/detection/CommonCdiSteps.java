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

import com.boozallen.drift.detection.cdi.TestCdiContainer;
import com.boozallen.drift.detection.cdi.TestCdiContext;
import com.boozallen.drift.detection.consumer.TestConsumer;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.jboss.weld.environment.se.WeldContainer;

public class CommonCdiSteps {

    private WeldContainer container;

    @Before("@cdi")
    public void setUp() {
        container = TestCdiContainer.create(new TestCdiContext());
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
