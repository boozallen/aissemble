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

import static com.boozallen.drift.detection.util.TestDataUtil.createRandomDouble;
import static com.boozallen.drift.detection.util.TestDataUtil.createRandomDriftVariable;
import static com.boozallen.drift.detection.util.TestDataUtil.createRandomDriftVariables;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;

import com.boozallen.drift.detection.algorithm.StandardDeviationDriftAlgorithm;
import com.boozallen.drift.detection.configuration.ShortHand;
import com.boozallen.drift.detection.data.DriftVariable;
import com.boozallen.drift.detection.data.DriftVariables;
import com.boozallen.drift.detection.policy.PolicyManager;
import com.boozallen.drift.detection.policy.json.PolicyInput;
import com.boozallen.drift.detection.policy.json.rule.PolicyRuleInput;
import com.boozallen.drift.detection.util.PolicyTestUtil;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class DetectDrift {

    private String policyIdentifier = RandomStringUtils.randomAlphabetic(3, 10);

    private static final String directory = "./target/drift-detection";

    private String inputName;

    private DriftDetectionResult result;

    private Double mean = createRandomDouble();

    private Double standardDeviation = createRandomDouble();

    @Given("a policy has been configured with a single rule")
    public void a_policy_has_been_configured_with_a_single_rule() {

        // We might be able to make this more random later but we don't have
        // many implemented at the moment
        PolicyInput policy = new PolicyInput(policyIdentifier);
        PolicyRuleInput rule = new PolicyRuleInput(ShortHand.STANDARD_DEVIATION.shortHand);
        Map<String, Object> configurations = new HashMap<String, Object>();
        configurations.put(StandardDeviationDriftAlgorithm.MEAN_CONFIGURATION, mean);
        configurations.put(StandardDeviationDriftAlgorithm.STANDARD_DEVIATION_CONFIGURATION, standardDeviation);
        rule.setConfiguration(configurations);
        policy.addRule(rule);

        PolicyTestUtil.writePolicyToFile(policy, directory, "test.json");

        PolicyManager.getInstance().loadPolicyConfigurations(directory);
    }

    @When("drift detection is invoked specifying the policy and a single input")
    public void drift_detection_is_invoked_specifying_the_policy_and_a_single_input() {

        // Create a random input
        DriftVariable<Double> input = createRandomDriftVariable();
        inputName = input.getName();

        // Call drift on it
        DriftDetector driftDetector = new DriftDetector();
        result = driftDetector.detect(policyIdentifier, input);
    }

    @When("drift detection is invoked specifying the policy and a list of inputs")
    public void drift_detection_is_invoked_specifying_the_policy_and_a_list_of_inputs() {

        // Create a random input
        DriftVariables<Double> input = createRandomDriftVariables();
        inputName = input.getName();

        DriftDetector detector = new DriftDetector();
        result = detector.detect(policyIdentifier, input);
    }

    @When("drift detection is invoked specifying the policy, a single input, and control data")
    public void drift_detection_is_invoked_specifying_the_policy_a_single_input_and_control_data() {

        // Create a random input
        DriftVariable<Double> input = createRandomDriftVariable();
        inputName = input.getName();

        // Create random control data
        DriftVariables<Double> control = createRandomDriftVariables();

        DriftDetector detector = new DriftDetector();
        result = detector.detect(policyIdentifier, input, control);
    }

    @When("drift detection is invoked specifying the policy, a list of inputs, and control data")
    public void drift_detection_is_invoked_specifying_the_policy_a_list_of_inputs_and_control_data() {

        // Create a random input
        DriftVariables<Double> input = createRandomDriftVariables();
        inputName = input.getName();

        // Create random control data
        DriftVariables<Double> control = createRandomDriftVariables();

        DriftDetector detector = new DriftDetector();
        result = detector.detect(policyIdentifier, input, control);
    }

    @Then("drift detection is run on the single input using the policy specified")
    public void drift_detection_is_run_on_the_single_input_using_the_policy_specified() {
        verifyResult();
    }

    @Then("drift detection is run on the list of inputs using the policy specified")
    public void drift_detection_is_run_on_the_list_of_inputs_using_the_policy_specified() {
        verifyResult();
    }

    @Then("the control data is used to set the metrics for the algorithm")
    public void the_control_data_is_used_to_set_the_metrics_for_the_algorithm() {
        Double actualMean = (Double) result.getMetadata().get(StandardDeviationDriftAlgorithm.MEAN_CONFIGURATION);
        Double actualStandardDeviation = (Double) result.getMetadata()
                .get(StandardDeviationDriftAlgorithm.STANDARD_DEVIATION_CONFIGURATION);
        assertNotEquals("Expected the mean to be overridden by the control data", mean, actualMean);
        assertNotEquals("Expected the standard deviation to be overridden by the control data", standardDeviation,
                actualStandardDeviation);
    }

    private void verifyResult() {

        // Make sure drift detection was run and there's a result
        assertNotNull("Drift detection result was null", result);
        assertNotNull("Expected drift detection to be have result of true or false", result.hasDrift());
        assertEquals("Policy used for drift detection did not match expected", policyIdentifier,
                result.getMetadata().get(DriftDetectionResult.POLICY_IDENTIFIER));
        assertEquals("The data input did not match the expected", inputName,
                result.getMetadata().get(DriftDetectionResult.DATA_NAME));
    }

}
