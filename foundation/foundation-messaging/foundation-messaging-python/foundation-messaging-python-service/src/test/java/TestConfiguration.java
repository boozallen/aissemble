/*-
 * #%L
 * aiSSEMBLE::Foundation::Messaging::Python::Service
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

import io.cucumber.plugin.EventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.TestRunFinished;
import io.cucumber.plugin.event.TestRunStarted;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;

/**
 * This class helps to setup the configuration before and after the cucumber tests
 */
public class TestConfiguration implements EventListener {

    // default constructor required for the plugin
    public TestConfiguration() {}

    @Override
    public void setEventPublisher(EventPublisher eventPublisher) {
        // setup before all cucumber tests
        eventPublisher.registerHandlerFor(TestRunStarted.class, this::applyBeforeTests);

        // cleanup after all cucumber tests
        eventPublisher.registerHandlerFor(TestRunFinished.class, this::applyAfterTests);
    }

    /**
     * Apply setup before the Cucumber tests
     */
    public void applyBeforeTests(TestRunStarted handler) {
        InMemoryConnector.switchOutgoingChannelsToInMemory("channel-A-out");
        InMemoryConnector.switchOutgoingChannelsToInMemory("channel-B-out");
        InMemoryConnector.switchOutgoingChannelsToInMemory("channel-C-out");
        InMemoryConnector.switchIncomingChannelsToInMemory("channel-A-in");
        InMemoryConnector.switchIncomingChannelsToInMemory("channel-B-in");
        InMemoryConnector.switchIncomingChannelsToInMemory("channel-C-in");
        InMemoryConnector.switchIncomingChannelsToInMemory("channel-another-in");
    }

    /**
     * Apply cleanup actions after the Cucumber tests
     */
    public void applyAfterTests(TestRunFinished handler) {
        InMemoryConnector.clear();
    }
}
