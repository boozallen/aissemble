package com.boozallen.aissemble.alerting.slack.cdi;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Alerting::Slack
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.spi.Extension;

import com.boozallen.aissemble.alerting.slack.consumer.SlackConsumer;
import com.boozallen.aissemble.core.cdi.CdiContext;

public class SlackCdiContext implements CdiContext {

    @Override
    public List<Class<?>> getCdiClasses() {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        classes.add(SlackConsumer.class);
        return classes;
    }

    @Override
    public List<Extension> getExtensions() {
        return null;
    }

}
