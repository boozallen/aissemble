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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

import org.apache.commons.lang3.RandomStringUtils;

import com.boozallen.drift.detection.algorithm.StandardDeviationDriftAlgorithm;
import com.boozallen.drift.detection.configuration.ShortHand;
import com.boozallen.drift.detection.data.DriftData;
import com.boozallen.drift.detection.data.DriftVariable;
import com.boozallen.drift.detection.data.DriftVariables;
import com.boozallen.drift.detection.policy.PolicyManager;
import com.boozallen.drift.detection.policy.json.PolicyInput;
import com.boozallen.drift.detection.policy.json.rule.PolicyRuleInput;
import com.boozallen.drift.detection.util.PolicyTestUtil;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class DetectDriftUsingStandardDeviationSteps {

    private PolicyInput policy;

    private String policyIdentifier = RandomStringUtils.randomAlphanumeric(5);

    private static final String directory = "./target/standard-deviation-drift-detection";

    private DriftDetectionResult result;

    private Double testValue = 100d;

    @Given("a policy with a single rule configured to use the standard deviation algorithm")
    public void a_policy_with_a_single_rule_configured_to_use_the_standard_score_algorithm() {
        createTestPolicy();
    }

    @Given("the algorithm has a configured mean of {int}, standard deviation of {int}, and zScore of {int}")
    public void the_algorithm_has_a_configured_mean_of_standard_deviation_of_and_z_score_of(Integer mean,
            Integer standardDeviation, Integer zScore) {
        Map<String, Object> configurations = new HashMap<String, Object>();
        configurations.put(StandardDeviationDriftAlgorithm.MEAN_CONFIGURATION, mean);
        configurations.put(StandardDeviationDriftAlgorithm.STANDARD_DEVIATION_CONFIGURATION, standardDeviation);
        configurations.put(StandardDeviationDriftAlgorithm.ZSCORE_CONFIGURATION, zScore);
        policy.getRules().get(0).setConfiguration(configurations);
    }

    @When("drift detection is invoked on {string} using the policy")
    public void drift_detection_is_invoked_on_using_the_policy(String text) {
        loadPolicy();

        // Create the test data
        DriftData data = createTestData(text);

        // Detect Drift
        DriftDetector driftDetector = new DriftDetector();
        result = driftDetector.detect(policyIdentifier, data);
    }

    @When("drift is detected on a input")
    public void drift_is_detected_on_a_input() {

        // Create some rules and test data that we know has drift
        createTestPolicy();
        Map<String, Object> configurations = new HashMap<String, Object>();
        configurations.put(StandardDeviationDriftAlgorithm.MEAN_CONFIGURATION, 5);
        configurations.put(StandardDeviationDriftAlgorithm.STANDARD_DEVIATION_CONFIGURATION, 3);
        configurations.put(StandardDeviationDriftAlgorithm.ZSCORE_CONFIGURATION, 2);
        policy.getRules().get(0).setConfiguration(configurations);
        loadPolicy();
        DriftData data = createTestData(testValue.toString());

        // Detect Drift
        DriftDetector driftDetector = new DriftDetector();
        result = driftDetector.detect(policyIdentifier, data);

        assertTrue("Drift should have been detected", result.hasDrift());
    }

    @Then("input whose values are between the upper bound of {int} and lower bound {int} will not be flagged for drift")
    public void input_whose_values_are_between_the_upper_bound_of_and_lower_bound_will_not_be_flagged_for_drift(
            Integer max, Integer min) {

    }

    @Then("input whose values fall outside the upper bound of {int} and lower bound {int} will be flagged for drift")
    public void input_whose_values_fall_outside_the_upper_bound_of_and_lower_bound_will_be_flagged_for_drift(
            Integer upperBound, Integer lowerBound) {
        // For explanation in the feature file
    }

    @Then("drift detected is {string}")
    public void drift_detected_is(String hasDrift) {
        assertNotNull("Drift detection result was unexpectedly null", result);
        assertEquals("Drift detection did not match expected", Boolean.parseBoolean(hasDrift), result.hasDrift());
    }

    @Then("the flagged value will be included in the result")
    public void the_flagged_value_will_be_included_in_the_result() {
        Map<String, Object> metadata = result.getMetadata();
        assertTrue("Result did not have a flagged value", metadata.containsKey(DriftDetectionResult.FLAGGED_VALUE));
        Object value = metadata.get(DriftDetectionResult.FLAGGED_VALUE);
        Double flaggedResult = (Double) value;
        assertEquals("Flagged value did not match expected", testValue, flaggedResult);
    }

    private DriftData createTestData(String text) {
        String[] values = text.split(",");
        DriftData data = null;
        if (values.length == 1) {
            data = createDriftVariable(values[0]);
        } else {
            List<DriftVariable<Double>> list = new ArrayList<DriftVariable<Double>>();
            for (int i = 0; i < values.length; i++) {
                list.add(createDriftVariable(values[i]));
            }
            DriftVariables<Double> variables = new DriftVariables<Double>(list);
            variables.setName(RandomStringUtils.randomAlphabetic(10));
            data = variables;
        }
        return data;
    }

    private DriftVariable<Double> createDriftVariable(String text) {
        DriftVariable<Double> variable = new DriftVariable<Double>(Double.parseDouble(text));
        variable.setName(RandomStringUtils.randomAlphabetic(4));
        return variable;
    }

    private void loadPolicy() {
        PolicyTestUtil.writePolicyToFile(policy, directory, "sample.json");
        PolicyManager instance = PolicyManager.getInstance();
        instance.loadPolicyConfigurations(directory);
    }

    private void createTestPolicy() {
        policy = new PolicyInput(policyIdentifier);
        PolicyRuleInput rule = new PolicyRuleInput(ShortHand.STANDARD_DEVIATION.shortHand);
        policy.addRule(rule);
    }

}
