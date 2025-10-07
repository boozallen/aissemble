package com.boozallen.aissemble.messaging.python;

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

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.Before;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ConfigurationSteps {

    MessagingService service;
    int topicCount = 0;

    @Before("@configure")
    public void setup() {
    }

    @After("@configure")
    public void cleanupMessaging() {
        topicCount = 0;
    }

    @Given("I have configured the messaging library to connect to {string}")
    public void i_have_configured_the_messaging_library_to_connect_to(String topicName) {
        // handled in config file
    }

    @When("the messaging service starts")
    public void the_messaging_service_starts() {
        service = MessagingService.getInstance();
    }

    @Then("the service creates a new emitter for {string}")
    public void the_service_creates_a_new_emitter_for(String topicName) {
        assertNotEquals(null, service.getEmitters().get(topicName));
    }

    @Given("I have configured the messaging library to connect to {int} topics")
    public void i_have_configured_the_messaging_library_to_connect_to_topics(Integer numTopics) {
        topicCount = numTopics;
    }
    @Then("the service creates an emitter for each topic")
    public void the_service_creates_an_emitter_for_each_topic() {
        assertEquals(topicCount, service.getEmitters().size());
    }

    @Then("the service creates a new listener for {string}")
    public void the_service_creates_a_new_listener_for(String topicName) {
        assertNotEquals(null, service.getListeners().get(topicName));
    }

    @Then("the service creates an listener for each topic")
    public void the_service_creates_an_listener_for_each_topic() {
        assertEquals(topicCount, service.getListeners().size());
    }
}
