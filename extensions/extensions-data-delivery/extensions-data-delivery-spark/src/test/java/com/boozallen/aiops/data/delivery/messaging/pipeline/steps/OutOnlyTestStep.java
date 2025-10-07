package com.boozallen.aiops.data.delivery.messaging.pipeline.steps;

/*-
 * #%L
 * AIOps Foundation::AIOps Data Delivery::Spark
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

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
