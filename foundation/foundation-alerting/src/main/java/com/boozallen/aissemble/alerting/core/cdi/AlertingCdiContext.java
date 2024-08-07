package com.boozallen.aissemble.alerting.core.cdi;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Alerting::Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.List;

import com.boozallen.aissemble.messaging.core.cdi.MessagingCdiContext;

import com.boozallen.aissemble.alerting.core.AlertProducer;
import com.boozallen.aissemble.alerting.core.AlertProducerApi;
import com.boozallen.aissemble.alerting.core.consumer.AlertLogger;

public class AlertingCdiContext extends MessagingCdiContext {

    @Override
    public List<Class<?>> getCdiClasses() {
        List<Class<?>> classes = super.getCdiClasses();
        classes.add(AlertLogger.class);
        classes.add(AlertProducer.class);
        classes.add(AlertProducerApi.class);
        return classes;
    }

}
