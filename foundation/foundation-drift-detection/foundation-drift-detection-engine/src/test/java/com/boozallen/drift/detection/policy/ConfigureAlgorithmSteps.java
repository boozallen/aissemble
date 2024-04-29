package com.boozallen.drift.detection.policy;

/*-
 * #%L
 * Drift Detection::Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import static com.boozallen.drift.detection.util.TestDataUtil.createRandomDouble;
import static com.boozallen.drift.detection.util.TestDataUtil.createRandomDriftVariable;
import static com.boozallen.drift.detection.util.TestDataUtil.createRandomDriftVariables;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;

import com.boozallen.drift.detection.DriftDetectionResult;
import com.boozallen.drift.detection.DriftDetector;
import com.boozallen.drift.detection.algorithm.DriftAlgorithm;
import com.boozallen.drift.detection.algorithm.StandardDeviationDriftAlgorithm;
import com.boozallen.drift.detection.configuration.ShortHand;
import com.boozallen.drift.detection.data.DriftVariable;
import com.boozallen.drift.detection.data.DriftVariables;
import com.boozallen.drift.detection.policy.json.PolicyInput;
import com.boozallen.drift.detection.policy.json.rule.PolicyRuleInput;
import com.boozallen.drift.detection.util.PolicyTestUtil;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ConfigureAlgorithmSteps {

    private PolicyInput policy;

    private static final String directory = "./target/configurations";

    private Double mean = createRandomDouble();

    private Double standardDeviation = createRandomDouble();

    private Double zScore = createRandomDouble();

    private String policyIdentifier = RandomStringUtils.randomAlphanumeric(5);

    private DriftDetectionResult result;

    @Given("a policy with one rule configured to use the standard deviation algorithm")
    public void a_policy_with_one_rule_configured_to_use_the_standard_deviation_algorithm() {
        createTestPolicy();
    }

    @Given("the rule specifies a mean and standard deviation")
    public void the_rule_specifies_a_mean_and_standard_deviation() {

        Map<String, Object> configurations = new HashMap<String, Object>();
        configurations.put(StandardDeviationDriftAlgorithm.MEAN_CONFIGURATION, mean);
        configurations.put(StandardDeviationDriftAlgorithm.STANDARD_DEVIATION_CONFIGURATION, standardDeviation);
        policy.getRules().get(0).setConfiguration(configurations);
    }

    @Given("the rule specifies a zScore")
    public void the_rule_specifies_a_z_score() {
        Map<String, Object> configurations = new HashMap<String, Object>();
        configurations.put(StandardDeviationDriftAlgorithm.MEAN_CONFIGURATION, mean);
        configurations.put(StandardDeviationDriftAlgorithm.STANDARD_DEVIATION_CONFIGURATION, standardDeviation);
        configurations.put(StandardDeviationDriftAlgorithm.ZSCORE_CONFIGURATION, zScore);
        policy.getRules().get(0).setConfiguration(configurations);
    }

    @When("drift detection is invoked using the policy")
    public void drift_detection_is_invoked_using_the_policy() {

        // Load up the policies
        loadPolicy();

        // Create some fake data
        DriftVariable<Double> value = createRandomDriftVariable();

        DriftDetector driftDetector = new DriftDetector();
        result = driftDetector.detect(policyIdentifier, value);
    }

    @When("drift detection is invoked using control data and the policy")
    public void drift_detection_is_invoked_using_control_data_and_the_policy() {

        // Load up the policies
        loadPolicy();

        // Create some fake data
        DriftVariables<Double> input = createRandomDriftVariables();
        DriftVariables<Double> control = createRandomDriftVariables();

        DriftDetector driftDetector = new DriftDetector();
        result = driftDetector.detect(policyIdentifier, input, control);
    }

    @When("the zScore is not configured by a rule")
    public void the_z_score_is_not_configured_by_a_rule() {

        createTestPolicy();

        // Ensure the policy is not configured
        Map<String, Object> configurations = new HashMap<String, Object>();
        policy.getRules().get(0).setConfiguration(configurations);

        // Load the policy
        loadPolicy();
    }

    @Then("the standard deviation will use the specified mean and standard deviation")
    public void the_standard_score_will_use_the_specified_mean_and_standard_deviation() {
        assertNotNull("Result was unexpectedly null", result);
        Map<String, Object> metadata = result.getMetadata();
        Double actualMean = (Double) metadata.get(StandardDeviationDriftAlgorithm.MEAN_CONFIGURATION);
        Double actualStandardDeviation = (Double) metadata.get(StandardDeviationDriftAlgorithm.STANDARD_DEVIATION_CONFIGURATION);
        assertEquals("Expected mean did not match actual mean used", mean, actualMean);
        assertEquals("Expected standard deviation did not match actual standard deviation used", standardDeviation,
                actualStandardDeviation);
    }

    @Then("the standard deviation will use the mean and standard deviation calculated from the control data")
    public void the_standard_score_will_use_the_mean_and_standard_deviation_calculated_from_the_control_data() {
        assertNotNull("Result was unexpectedly null", result);
        Map<String, Object> metadata = result.getMetadata();
        Double actualMean = (Double) metadata.get(StandardDeviationDriftAlgorithm.MEAN_CONFIGURATION);
        Double actualStandardDeviation = (Double) metadata.get(StandardDeviationDriftAlgorithm.STANDARD_DEVIATION_CONFIGURATION);
        assertNotEquals("Expected that configured mean would be overridden by control mean", mean, actualMean);
        assertNotEquals("Expected that configured standard deviation would be overridden by control standard deviation",
                standardDeviation, actualStandardDeviation);
    }

    @Then("the zScore is set to {int} by default")
    public void the_z_score_is_set_to_by_default(Integer defaultZScore) {
        verifyZScore(new Double(defaultZScore.doubleValue()));
    }

    @Then("the standard deviation will use the zScore configured by the rule")
    public void the_standard_score_will_use_the_z_score_configured_by_the_rule() {
        verifyZScore(zScore);
    }

    private void verifyZScore(Double expectedZScore) {
        PolicyManager manager = PolicyManager.getInstance();
        DriftDetectionPolicy policy = manager.getPolicy(policyIdentifier);
        List<PolicyRule> rules = policy.getRules();
        assertEquals("There was unexpectedly more than one rule", 1, rules.size());
        DriftAlgorithm algorithm = rules.get(0).getAlgorithm();
        assertTrue("Expected the rule to use the standard deviation algorithm", algorithm instanceof StandardDeviationDriftAlgorithm);
        StandardDeviationDriftAlgorithm standardDeviation = (StandardDeviationDriftAlgorithm) algorithm;
        Double actualZScore = standardDeviation.getZScore();
        assertEquals("ZScore was unexpectedly not set to the default", expectedZScore, actualZScore);
    }

    private void createTestPolicy() {
        policy = new PolicyInput(policyIdentifier);
        PolicyRuleInput rule = new PolicyRuleInput(ShortHand.STANDARD_DEVIATION.shortHand);
        policy.addRule(rule);
    }

    private void loadPolicy() {
        PolicyTestUtil.writePolicyToFile(policy, directory, "sample.json");
        PolicyManager instance = PolicyManager.getInstance();
        instance.loadPolicyConfigurations(directory);
    }

}
