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

import com.boozallen.aissemble.messaging.python.exception.TopicNotSupportedError;
import com.boozallen.aissemble.messaging.python.transfer.Callback;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.Before;
import io.cucumber.java.After;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

public class SubscribeSteps {
    MessagingService messagingService;
    private Exception foundError;
    Callback callback;
    int ackStrategyIndex;

    @Before("@subscribe")
    public void setup() {
        messagingService = MessagingService.getInstance();
    }

    @After("@send")
    public void cleanupMessaging() {
        foundError = null;
    }

    @Given("a messaging topic {string}")
    public void a_messaging_topic(String topic) {
        // set in CDI
    }

    @And("a valid callback and ack strategy")
    public void a_valid_callback_and_ack_strategy() {
        callback = null;
        ackStrategyIndex = 0;
    }

    @When("the service creates a subscription to {string}")
    public void the_service_creates_a_subscription_to(String topic) {
        try {
            messagingService.subscribe(topic, callback, ackStrategyIndex);
        } catch (TopicNotSupportedError error) {
            foundError = error;
        }
    }

    @Then("the service is subscribed to {string}")
    public void the_service_is_subscribed_to(String topic) {
        try {
            assertTrue(messagingService.isTopicSubscribed(topic));
        } catch (TopicNotSupportedError error) {
            foundError = error;
        }
    }

    @Then("an exception is thrown saying the topic does not exist")
    public void an_exception_is_thrown_saying_the_topic_does_not_exist() {
        assertEquals(TopicNotSupportedError.class, foundError.getClass());
    }
}
