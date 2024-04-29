package com.boozallen.aissemble.alerting.core;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Alerting::Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.jboss.weld.environment.se.WeldContainer;

import com.boozallen.aissemble.alerting.core.Alert.Status;
import com.boozallen.aissemble.alerting.core.cdi.AlertingCdiContainer;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class AlertingSteps {

    private Status alertStatus;
    private String alertMessage;
    private UUID alertId;

    private WeldContainer container;

    @Before("@alerting")
    public void setUp() {
        AlertingTestCdiContext context = new AlertingTestCdiContext();
        container = AlertingCdiContainer.create(context);
    }

    @After("@alerting")
    public void tearDown() {
        if (container != null) {
            container.close();
            container = null;
        }
    }

    @Given("^I am subscribed to the alert topic$")
    public void i_am_subscribed_to_the_alert_topic() throws Throwable {
        // Happens automatically
    }

    @When("^an alert is published to the topic$")
    public void an_alert_is_published_to_the_topic() throws Throwable {

        // send alert to the topic
        alertStatus = Status.SUCCESS;
        alertMessage = "this is the alert message";

        AlertProducer alertProducer = container.select(AlertProducer.class).get();
        assertNotNull("Alert producer was unexpectedly null", alertProducer);
        alertId = alertProducer.sendAlert(alertStatus, alertMessage);
    }

    @Then("^I can see the details of the alert$")
    public void i_can_see_the_details_of_the_alert() throws Throwable {

        // verify that the consumer received the alert that was published in the
        // previous step
        Alert testAlert = null;

        for (Alert alert : AlertCounter.getAllAlerts()) {
            if (alert.getId().equals(alertId)) {
                testAlert = alert;
                break;
            }
        }

        assertNotNull("Expected alert was not received", testAlert);
        assertEquals("Unexpected alert status received", alertStatus, testAlert.getStatus());
        assertEquals("Unexpected alert message received", alertMessage, testAlert.getMessage());
    }

}
