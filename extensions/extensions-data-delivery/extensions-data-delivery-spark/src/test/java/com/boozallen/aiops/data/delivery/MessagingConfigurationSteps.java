package com.boozallen.aiops.data.delivery;

/*-
 * #%L
 * AIOps Foundation::AIOps Data Delivery::Spark
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.data.delivery.messaging.PipelineMessagingConfig;
import com.boozallen.aiops.data.delivery.messaging.pipeline.steps.AbstractTestStep;
import com.boozallen.aiops.data.delivery.messaging.pipeline.steps.InOnlyTestStep;
import com.boozallen.aiops.data.delivery.messaging.pipeline.steps.InOutTestStep;
import com.boozallen.aiops.data.delivery.messaging.pipeline.steps.NoChannelTestStep;
import com.boozallen.aiops.data.delivery.messaging.pipeline.steps.OutOnlyTestStep;
import com.boozallen.aiops.data.delivery.messaging.pipeline.steps.OverrideTestStep;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySink;
import io.smallrye.reactive.messaging.memory.InMemorySource;
import org.apache.commons.lang.RandomStringUtils;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.Assert;

import jakarta.enterprise.inject.Any;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MessagingConfigurationSteps {
    private WeldContainer container;
    private InMemoryConnector connector;
    private AbstractTestStep step;
    private InMemorySource<Object> source;
    private InMemorySink<Object> sink;
    private String testInput;

    @Before("@messaging")
    public void setUp() {
        //In a generated pipeline, this is handled by the DefaultPipelineConfig class
        PipelineMessagingConfig.registerStep(InOnlyTestStep.class.getSimpleName(), InOnlyTestStep.INCOMING_CHANNEL, null);
        PipelineMessagingConfig.registerStep(InOutTestStep.class.getSimpleName(), InOutTestStep.INCOMING_CHANNEL, InOutTestStep.OUTGOING_CHANNEL);
        PipelineMessagingConfig.registerStep(NoChannelTestStep.class.getSimpleName(), null, null);
        PipelineMessagingConfig.registerStep(OutOnlyTestStep.class.getSimpleName(), null, OutOnlyTestStep.OUTGOING_CHANNEL);
        PipelineMessagingConfig.registerStep(OverrideTestStep.class.getSimpleName(), OverrideTestStep.INCOMING_CHANNEL, OverrideTestStep.OUTGOING_CHANNEL);

        container = CdiContainerFactory.getCdiContainer(Collections.singletonList(new TestCdiContext()));
        connector = container.select(InMemoryConnector.class, new Any.Literal()).get();
    }

    @After("@messaging")
    public void tearDown() {
        container.shutdown();
    }

    @Given("^a step with outgoing and incoming messaging$")
    public void aStepWithOutgoingAndIncomingMessaging() {
        step = container.select(InOutTestStep.class, new Any.Literal()).get();
        source = connector.source(step.getIncomingChannel());
        sink = connector.sink(step.getOutgoingChannel());
    }

    @Given("a step with incoming messaging")
    public void aStepWithIncomingMessaging() {
        step = container.select(InOnlyTestStep.class, new Any.Literal()).get();
        source = connector.source(step.getIncomingChannel());
    }

    @Given("a step with outgoing messaging")
    public void aStepWithOutgoingMessaging() {
        step = container.select(OutOnlyTestStep.class, new Any.Literal()).get();
        sink = connector.sink(step.getOutgoingChannel());
    }

    @Given("a step with override configurations")
    public void aStepWithOverrideConfigurations() {
        step = container.select(OverrideTestStep.class, new Any.Literal()).get();
        source = connector.source(step.getIncomingChannel());
        sink = connector.sink(step.getOutgoingChannel());
    }

    @Given("a pipeline configuration for the step")
    public void aPipelineConfigurationForTheStep() {
    }

    @When("messages flow from the external system")
    public void messagesFlowFromTheExternalSystem() {
        testInput = "Test Message-" + RandomStringUtils.randomAlphanumeric(5);
        source.send(testInput);
        source.complete();
    }

    @When("the step is executed")
    public void theStepIsExecuted() {
        testInput = "Test Message-" + RandomStringUtils.randomAlphanumeric(5);
        step.executeStep(testInput);
    }

    @When("the outgoing channel name is changed")
    public void theOutgoingChannelNameIsChanged() {
    }

    @Then("the configuration directs the messaging to the step")
    public void theConfigurationDirectsTheMessagingToTheStep() {
        Assert.assertEquals("External messages not received by step", Collections.singletonList(testInput), step.received());
    }

    @Then("the configuration directs the step result to the external system")
    public void theConfigurationDirectsTheStepResultToTheExternalSystem() {
        List<String> externalMessages = sink.received().stream()
                .map(Message::getPayload)
                .map(String.class::cast)
                .collect(Collectors.toList());
        Assert.assertEquals("The step output was not sent to the external system", step.sent(), externalMessages);
    }
}
