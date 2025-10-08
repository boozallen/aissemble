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
