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

import org.eclipse.microprofile.reactive.messaging.Incoming;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class InOnlyTestStep extends AbstractTestStep {
    public static final String INCOMING_CHANNEL = "test-in-channel-InOnly";

    protected InOnlyTestStep() {
        super("test", "InOnlyTestStep");
    }

    @Override
    public String getIncomingChannel() {
        return INCOMING_CHANNEL;
    }

    @Override
    public String getOutgoingChannel() {
        return null;
    }

    @Override
    @Incoming(INCOMING_CHANNEL)
    public String executeStep(String input) {
        return super.executeStep(input);
    }
}
