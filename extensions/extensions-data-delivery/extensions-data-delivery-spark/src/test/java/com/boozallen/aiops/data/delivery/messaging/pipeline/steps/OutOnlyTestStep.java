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

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class OutOnlyTestStep extends AbstractTestStep {
    public static final String OUTGOING_CHANNEL = "test-out-channel-OutOnly";

    // Helps test that emitter channel names are picked up
    @OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = 20)
	@Inject @Channel(OUTGOING_CHANNEL)
    Emitter<String> emitterWithBuffer;

    protected OutOnlyTestStep() {
        super("test", "OutOnlyTestStep");
    }

    @Override
    public String getIncomingChannel() {
        return null;
    }

    @Override
    public String getOutgoingChannel() {
        return OUTGOING_CHANNEL;
    }

    @Override
    public String executeStep(String input) {
        String output = super.executeStep(input);
        emitterWithBuffer.send(output);
        return output;
    }

}
