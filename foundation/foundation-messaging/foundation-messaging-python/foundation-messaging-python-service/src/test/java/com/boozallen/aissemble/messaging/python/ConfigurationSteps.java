package com.boozallen.aissemble.messaging.python;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Messaging::Python::Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.smallrye.reactive.messaging.providers.connectors.InMemoryConnector;
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
