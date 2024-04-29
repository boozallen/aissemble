package com.boozallen.drift.detection;

/*-
 * #%L
 * Drift Detection::Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;

import com.boozallen.drift.detection.algorithm.StandardDeviationDriftAlgorithm;
import com.boozallen.drift.detection.configuration.ShortHand;
import com.boozallen.drift.detection.consumer.TestConsumer;
import com.boozallen.drift.detection.data.DriftVariable;
import com.boozallen.drift.detection.policy.AlertOptions;
import com.boozallen.drift.detection.policy.PolicyManager;
import com.boozallen.drift.detection.policy.json.PolicyInput;
import com.boozallen.drift.detection.policy.json.rule.PolicyRuleInput;
import com.boozallen.drift.detection.util.PolicyTestUtil;
import com.boozallen.aissemble.alerting.core.Alert;
import com.boozallen.aissemble.alerting.core.Alert.Status;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class SendAlerts {

    private PolicyInput policy;

    private String policyIdentifier = RandomStringUtils.randomAlphabetic(3, 10);

    private String directory = "./target/alerts";

    private static final double mean = 10d;

    private static final double standardDeviation = 2d;

    private static final double okInput = 11d;

    private static final double badInput = 13d;

    private String driftStatus = "drift is detected";

    @Given("a policy has been defined with no alert configuration")
    public void a_policy_has_been_defined_with_no_alert_configuration() {
        createTestPolicy();
        loadPolicy();
    }

    @Given("a policy has been configured to only send alerts when drift is detected")
    public void a_policy_has_been_configured_to_only_send_alerts_when_drift_is_detected() {
        createTestPolicy();
        policy.setShouldSendAlert(AlertOptions.ON_DRIFT);
        loadPolicy();
    }

    @Given("a policy has been configured to never send alerts")
    public void a_policy_has_been_configured_to_never_send_alerts() {
        createTestPolicy();
        policy.setShouldSendAlert(AlertOptions.NEVER);
        loadPolicy();
    }

    @When("an alert is sent")
    public void an_alert_is_sent() {
        DriftDetector detector = new DriftDetector();
        DriftDetectionResult result = new DriftDetectionResult();
        result.setDriftDiscovered(false);
        detector.publishAlert(result);
    }

    @When("{string} using the policy specified")
    public void using_the_policy_specified(String driftDetected) {

        DriftVariable<Double> input;
        if (driftStatus.equals(driftDetected)) {
            input = new DriftVariable<Double>(badInput);
            input.setName("I've drifted");
        } else {
            input = new DriftVariable<Double>(okInput);
            input.setName("I'm an ok input");
        }

        // Detect drift on our input using the policy
        DriftDetector driftDetector = new DriftDetector();
        driftDetector.detect(policyIdentifier, input);
    }

    @Then("the alert is published to an {string} topic")
    public void the_alert_is_published_to_an_topic(String topic) {

        List<Alert> alerts = TestConsumer.getAllAlerts();
        assertEquals("Number of alerts did not match expected", 1, alerts.size());
    }

    @Then("an alert is sent that {string}")
    public void an_alert_is_set_that(String driftDetected) {
        if (driftStatus.equals(driftDetected)) {
            verifyAlert(Status.FAILURE);
        } else {
            verifyAlert(Status.SUCCESS);
        }
    }

    @Then("an alert {string} sent")
    public void an_alert_sent(String alertStatus) {
        if ("is".equals(alertStatus)) {
            verifyAlert(Status.FAILURE);
        } else {
            verifyNoAlertSent();
        }
    }

    @Then("an alert is not sent")
    public void an_alert_is_not_sent() {
        verifyNoAlertSent();
    }

    private void verifyNoAlertSent() {
        List<Alert> alerts = TestConsumer.getAllAlerts();
        assertEquals("Expected zero alerts to be sent", 0, alerts.size());
    }

    private void verifyAlert(Status status) {
        List<Alert> alerts = TestConsumer.getAllAlerts();
        assertEquals("Number of alerts did not match expected", 1, alerts.size());
        Alert alert = alerts.get(0);
        assertEquals("Alert status did not match expected", status, alert.getStatus());
    }

    private void loadPolicy() {
        PolicyTestUtil.writePolicyToFile(policy, directory, "sample.json");
        PolicyManager.getInstance().loadPolicyConfigurations(directory);
    }

    private void createTestPolicy() {

        policy = new PolicyInput(policyIdentifier);
        PolicyRuleInput rule = new PolicyRuleInput(ShortHand.STANDARD_DEVIATION.shortHand);

        // Set these so we can figure out what we need to have it detect drift
        // or not
        Map<String, Object> configurations = new HashMap<String, Object>();
        configurations.put(StandardDeviationDriftAlgorithm.MEAN_CONFIGURATION, mean);
        configurations.put(StandardDeviationDriftAlgorithm.STANDARD_DEVIATION_CONFIGURATION, standardDeviation);
        rule.setConfiguration(configurations);
        policy.addRule(rule);
    }

}
