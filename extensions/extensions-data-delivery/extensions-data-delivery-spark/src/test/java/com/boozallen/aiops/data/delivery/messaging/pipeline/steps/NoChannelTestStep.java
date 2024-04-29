package com.boozallen.aiops.data.delivery.messaging.pipeline.steps;

/*-
 * #%L
 * AIOps Foundation::AIOps Data Delivery::Spark
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class NoChannelTestStep extends AbstractTestStep {
    protected NoChannelTestStep() {
        super("test", "NoChannelTestStep");
    }

    @Override
    public String getIncomingChannel() {
        return null;
    }

    @Override
    public String getOutgoingChannel() {
        return null;
    }
}
