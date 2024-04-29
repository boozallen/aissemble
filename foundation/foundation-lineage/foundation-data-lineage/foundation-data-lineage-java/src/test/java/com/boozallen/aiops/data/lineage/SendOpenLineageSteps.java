package com.boozallen.aiops.data.lineage;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Data Lineage
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.core.cdi.CdiContainer;
import com.boozallen.aiops.data.lineage.cdi.DataLineageCdiContext;
import com.boozallen.aiops.data.lineage.cdi.DataLineageCdiSelector;
import com.boozallen.aiops.data.lineage.cdi.TestCdiContext;
import com.boozallen.aiops.data.lineage.cdi.TestDataLineageConsoleEmissionCdiContext;
import com.boozallen.aiops.data.lineage.config.ConfigUtil;
import com.boozallen.aiops.data.lineage.test.transport.TestConsoleTransport;
import com.boozallen.aiops.data.lineage.transport.ConsoleTransport;
import com.boozallen.aissemble.common.Constants;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.smallrye.reactive.messaging.providers.connectors.InMemoryConnector;
import org.jboss.weld.environment.se.WeldContainer;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.UnsatisfiedResolutionException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.UUID;


public class SendOpenLineageSteps {
    static ConfigUtil util = ConfigUtil.getInstance();
    private RunEvent testRunEvent;
    private WeldContainer container;

    @Before
    public void reset() {
        TestConsoleTransport.hasReceivedMessage = false;
    }

    @Before("@emission")
    public void setup() {
        System.setProperty("ENABLE_LINEAGE", "true");
    }

    @Before("@withoutConsole")
    public void noConsole() {
        InMemoryConnector.switchOutgoingChannelsToInMemory(Constants.DATA_LINEAGE_CHANNEL_NAME);
        container = CdiContainer.create(List.of(new TestCdiContext(), new DataLineageCdiContext()));
    }

    @After("@cdi")
    public void cleanupCdi() {
        container.shutdown();
    }

    @After("@in-memory")
    public void cleanupMessaging() {
        InMemoryConnector.clear();
    }

    @Given("a run event")
    public void a_run_event() {
        UUID testId = UUID.randomUUID();
        RunFacet runFacet = new TestRunFacet();
        Map<String, RunFacet> runFacets = new HashMap<String, RunFacet>();
        runFacets.put("runFacet", runFacet);
        Run testRun = new Run(testId, runFacets);

        JobFacet jobFacet = new TestJobFacet();
        Map<String, JobFacet> jobFacets = new HashMap<String, JobFacet>();
        jobFacets.put("jobFacet", jobFacet);
        Job testJob = new Job("test job name", jobFacets, "test_namespace");
        testRunEvent = new RunEvent(testRun, testJob, "START");
    }

    @When("the ConsoleTransport class is added to the CDI container")
    public void console_transport_added_to_cdi() {
        container = CdiContainer.create(List.of(new TestCdiContext(), new TestDataLineageConsoleEmissionCdiContext()));
    }

    @When("the event is emitted via messaging")
    public void the_event_is_emitted_via_messaging() {
        EventEmitter.emitEvent(testRunEvent);
    }

    @Then("the event is sent over the outbound channel")
    public void the_event_is_sent_on_the_outbound_channel() {
        InMemoryConnector connector = container.select(InMemoryConnector.class, new Any.Literal()).get();
        assert(connector.sink(Constants.DATA_LINEAGE_CHANNEL_NAME).received().size() != 0);
    }

    @Then("the event is received for logging to the console")
    public void the_event_is_received_for_logging() {
        assert(TestConsoleTransport.hasReceivedMessage);
    }

    @Then("the event is not received for logging to the console")
    public void the_event_is_not_received_for_logging() {
        assert(!TestConsoleTransport.hasReceivedMessage);
    }

    @Given("a configuration that opts into console emission")
    public void configuration_opts_into_console_emission() {
        util.krausening.getProperties("data-lineage.properties").setProperty(
                "data.lineage.emission.console", "true"
        );
    }

    @Given("a configuration that opts out of console emission")
    public void configuration_opts_out_of_console_emission() {
        util.krausening.getProperties("data-lineage.properties").setProperty(
                "data.lineage.emission.console", "false"
        );
    }

    @When("the CDI container is created with automatically selected context")
    public void cdi_container_is_created_with_automatic_context() {
        container = CdiContainer.create(List.of(new TestCdiContext(), DataLineageCdiSelector.getDataLineageCdiContext()));
    }

    @Then("the ConsoleTransport class is available")
    public void consoleTransportIsAvailable() {
        assert(null != container.select(ConsoleTransport.class).get());
    }

    @Then("the ConsoleTransport class is not available")
    public void consoleTransportIsNotAvailable() {
        boolean retrievedConsoleTransportSuccessfully;
        try {
            container.select(ConsoleTransport.class).get();
            retrievedConsoleTransportSuccessfully = true;
        } catch(UnsatisfiedResolutionException ex) {
            retrievedConsoleTransportSuccessfully = false;
        }
        assert(!retrievedConsoleTransportSuccessfully);
    }

    private static class TestRunFacet extends RunFacet {
    }

    private static class TestJobFacet extends JobFacet {
    }
}
