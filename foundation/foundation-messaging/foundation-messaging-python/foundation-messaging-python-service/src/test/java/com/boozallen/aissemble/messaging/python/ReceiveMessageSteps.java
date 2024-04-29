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
import com.boozallen.aissemble.messaging.python.transfer.MessageHandle;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.smallrye.reactive.messaging.providers.connectors.InMemoryConnector;
import org.eclipse.microprofile.reactive.messaging.Message;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.CDI;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReceiveMessageSteps {
    private MessagingService gateway;
    private InMemoryConnector connector;
    private Callback consumerCallback;
    private AtomicBoolean ackIsSentToBroker;
    private AtomicBoolean nackIsSentToBroker;
    private String payloadReceived;
    private int ackStrategy;
    private String payload;

    @Before(value = "@receive-message")
    public void setup() {
        payload = "test message";
        gateway = MessagingService.getInstance();
        connector = CDI.current().select(InMemoryConnector.class, new Any.Literal()).get();
        consumerCallback = createTestCallback();
        ackIsSentToBroker = new AtomicBoolean();
        nackIsSentToBroker = new AtomicBoolean();
        ackStrategy = AckStrategy.POSTPROCESSING.getIndex();
    }

    @Before(value = "@nack-message", order = 10001)
    public void setupNackMessageTest(){
        payload = "exception";
    }

    @After(value = "@nack-message", order = 10001)
    public void resetConnector() {
        // clear out connector after nack test
        InMemoryConnector.clear();
        InMemoryConnector.switchIncomingChannelsToInMemory("channel-A-in");
    }

    @Before(value = "@manual-acknowledgement", order = 10002)
    public void configManualAcknowledgement() {
        ackStrategy = AckStrategy.MANUAL.getIndex();
    }

    @After("@receive-message")
    public void cleanup() {
        payload = null;
        connector = null;
        consumerCallback = null;
        ackIsSentToBroker = null;
        nackIsSentToBroker = null;
        gateway = null;
        ackStrategy = -1;
    }

    @Given("consumer subscribe to a topic named {string}")
    public void consumer_subscribe_to_a_topic_named(String topic) throws TopicNotSupportedError {
        gateway.subscribe(topic, consumerCallback, ackStrategy);
    }

    @When("a message is sent to the topic")
    public void a_message_is_sent_to_the_topic() {
        assertFalse("message has not been acked", ackIsSentToBroker.get());
        sendMessage(payload);
    }

    @Then("the message is processed successfully by the consumer")
    public void the_message_is_processed_successfully_by_the_consumer() {
        assertEquals(payloadReceived, payload);
    }

    @Then("an ack is sent to broker")
    public void an_ack_is_sent_to_broker() {
        assertTrue("message has been acked and return to broker", ackIsSentToBroker.get());
    }

    @Then("consumer failed to process the message")
    public void consumer_failed_to_process_message() {
        // consumer expect to throw exception when processing message
    }

    @Then("a nack is sent to broker")
    public void an_nack_is_sent_to_broker() {
        assertTrue("message has been nacked and return to broker", nackIsSentToBroker.get());
    }

    @Given("the subscription is configured with the manual ack strategy")
    public void the_subscription_is_configured_with_manual_ack_strategy() {
        // step was set in configManualAcknowledgement()
    }

    @When("a message is received from the topic")
    public void a_message_is_received_for_the_topic() {
        // set message to the channel
        sendMessage(payload);
        // verify message is received by service and passed to consumer
        assertEquals(payloadReceived, payload);
    }

    @Then("the service does not ack or nack the message")
    public void the_service_does_not_ack_or_nack_the_msg() {
        assertFalse("service does not nack the message", nackIsSentToBroker.get());
        assertFalse("service does not ack the message", ackIsSentToBroker.get());
    }

    @When("the consumer {string} the message")
    public void the_consumer_ack_or_nack_the_message(String acknowledge) {
        sendMessage(acknowledge);
    }

    @Then("the {string} is sent to the broker")
    public void the_ack_or_nack_is_sent_to_the_broker(String acknowledge) {
        String assertMessage = String.format("the %s is sent to the broker", acknowledge);
        AtomicBoolean atomicBoolean = (acknowledge.equals("ack")? ackIsSentToBroker: nackIsSentToBroker);
        assertTrue(assertMessage, atomicBoolean.get());
    }

    private void sendMessage(String payload) {
        connector.source("channel-A-in").send(createTestMessage(payload));
    }

    private Message<String> createTestMessage(String payload) {
        return Message.of(payload, () -> {
                ackIsSentToBroker.set(true);
                return CompletableFuture.completedFuture(null);
            },
            t -> {
                nackIsSentToBroker.set(true);
                return CompletableFuture.completedFuture(null);
            }
        );
    }

    private Callback createTestCallback() {
        return message -> {
            payloadReceived = (String)message.getPayload();
            switch (payloadReceived) {
                case "exception":
                    throw new RuntimeException("test exception");
                case "ack":
                    return message.ack();
                case "nack":
                    return message.nack("testing manual nack");
            }
            return CompletableFuture.completedFuture(null);
        };
    }
}
