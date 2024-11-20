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
import com.boozallen.aissemble.messaging.python.channel.ChannelFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;

import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.spi.CDI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SendMessageSteps {

    MessagingService gateway;
    Exception foundError;

    @Before("@send")
    public void setup() {
        gateway = MessagingService.getInstance();
    }

    @After("@send")
    public void cleanupMessaging() {
        foundError = null;
    }

    @Given("a messaging topic named {string}")
    public void a_messaging_topic_named(String topic) {
        // Had to be done in the before for CDI purposes
    }

    @When("a message is queued to {string}")
    public void a_message_is_queued_to(String topic) {
        try {
            gateway.publish(topic, "test message");
        } catch (TopicNotSupportedError error) {
            foundError = error;
        }
    }

    @Then("a message is sent to to the topic {string}")
    public void aMessageIsSentToToTheTopic(String topic) {
        assertNull(foundError);
        InMemoryConnector connector = CDI.current().select(InMemoryConnector.class, new Any.Literal()).get();
        assert(connector.sink(ChannelFactory.loadOutgoingChannels().get(topic)).received().size() != 0);
    }

    @Then("an exception is thrown saying the topic was not found")
    public void anExceptionIsThrownSayingTheTopicWasNotFound() {
        assertEquals(TopicNotSupportedError.class, foundError.getClass());
    }
}
