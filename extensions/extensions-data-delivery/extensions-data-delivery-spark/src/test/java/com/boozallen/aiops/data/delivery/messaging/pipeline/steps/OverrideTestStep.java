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
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OverrideTestStep extends AbstractTestStep {
    public static final String INCOMING_CHANNEL = "test-in-channel-Override" ;
    public static final String OUTGOING_CHANNEL = "test-out-channel-Override" ;

    protected OverrideTestStep() {
        super("test", "OverrideTestStep");
    }

    @Override
    public String getIncomingChannel() {
        return INCOMING_CHANNEL;
    }

    @Override
    public String getOutgoingChannel() {
        return OUTGOING_CHANNEL;
    }

    @Override
    @Incoming(INCOMING_CHANNEL)
    @Outgoing(OUTGOING_CHANNEL)
    public String executeStep(String input) {
        return super.executeStep(input);
    }
}
