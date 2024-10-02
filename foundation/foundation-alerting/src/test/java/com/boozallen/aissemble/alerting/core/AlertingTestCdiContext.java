package com.boozallen.aissemble.alerting.core;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Alerting
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.List;
import com.boozallen.aissemble.alerting.core.cdi.AlertingCdiContext;

public class AlertingTestCdiContext extends AlertingCdiContext {

    @Override
    public List<Class<?>> getCdiClasses() {
        List<Class<?>> classes = super.getCdiClasses();
        classes.add(AlertCounter.class);
        return classes;
    }
}
