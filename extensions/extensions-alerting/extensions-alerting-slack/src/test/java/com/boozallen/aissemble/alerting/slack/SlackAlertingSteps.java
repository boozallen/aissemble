package com.boozallen.aissemble.alerting.slack;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Alerting::Slack
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.boozallen.aissemble.alerting.slack.cdi.SlackCdiContext;
import com.boozallen.aissemble.alerting.slack.config.SlackConfig;
import org.aeonbits.owner.KrauseningConfigFactory;
import org.jboss.weld.environment.se.WeldContainer;

import com.boozallen.aissemble.alerting.core.Alert;
import com.boozallen.aissemble.alerting.core.Alert.Status;
import com.boozallen.aissemble.alerting.core.cdi.AlertingCdiContainer;
import com.boozallen.aiops.core.cdi.CdiContainer;
import com.boozallen.aiops.core.cdi.CdiContext;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class SlackAlertingSteps {
    private WeldContainer container;

    private ChatPostMessageResponse slackResponse;

    private static SlackConfig slackConfig = KrauseningConfigFactory.create(SlackConfig.class);

    @Before("@alerting")
    public void setUp() {
        container = AlertingCdiContainer.create(new SlackCdiContext());
    }

    @After("@alerting")
    public void tearDown() {
        if (container != null) {
            container.close();
            container = null;
        }
    }

    @When("an alert is sent to slack")
    public void an_alert_is_sent_to_slack() {

        Alert alert = new Alert();
        alert.setId(UUID.randomUUID());
        alert.setStatus(Status.FAILURE);
        alert.setMessage("Integration Test Output, please ignore.");
        slackResponse = SlackClient.sendSlackMessage(alert, slackConfig.getClientHostId());
    }

    @Then("the alert is sent to the configured slack channel successfully")
    public void the_alert_is_sent_to_the_configured_slack_channel_successfully() {
        assertNotNull("Slack response was unexpectedly null", slackResponse);
        assertTrue("slack message was sent ok", slackResponse.isOk());
    }

}
