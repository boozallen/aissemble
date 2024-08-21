package com.boozallen.drift.detection.service;

/*-
 * #%L
 * Drift Detection::Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import jakarta.inject.Inject;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boozallen.drift.detection.DriftDetectionResult;
import com.boozallen.drift.detection.algorithm.StandardDeviationDriftAlgorithm;
import com.boozallen.drift.detection.configuration.ShortHand;
import com.boozallen.drift.detection.data.DriftData;
import com.boozallen.drift.detection.data.DriftDataInput;
import com.boozallen.drift.detection.policy.PolicyManager;
import com.boozallen.drift.detection.policy.json.PolicyInput;
import com.boozallen.drift.detection.policy.json.rule.PolicyRuleInput;
import com.boozallen.drift.detection.util.PolicyTestUtil;
import com.boozallen.drift.detection.util.TestDataUtil;
import com.boozallen.aissemble.alerting.core.Alert.Status;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class DriftDetectionServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(DriftDetectionServiceTest.class);

    private static final String directory = "./target/policies";

    private static final int mean = 20;

    private static final int standardDeviation = 10;

    private static final int zScore = 2;

    private DriftDetectionResult result;

    private String policyIdentifier = RandomStringUtils.randomAlphanumeric(5);

    @Inject
    DriftDetectionService driftDetectionService;

    /**
     * Currently, quarkus does not support cucumber. This is a placeholder for
     * the methods that will be used when this test is transitioned to cucumber
     * at a later date.
     */
    @Test
    public void testDriftInvocationEndpointWithMultipleDriftVariables() {
        a_policy_has_been_defined_for_detecting_drift();
        i_invoke_drift_specifying_the_policy_using_the_rest_service_with_multiple_drift_variables();
        i_receive_the_results_of_drift_detection();
    }

    @Test
    public void testDriftInvocationEndpointWithASingleDriftVariable() {
        a_policy_has_been_defined_for_detecting_drift();
        i_invoke_drift_specifying_the_policy_using_the_rest_service_with_a_single_drift_variable();
        i_receive_the_results_of_drift_detection();
    }

    public void a_policy_has_been_defined_for_detecting_drift() {
        createTestPolicy();
        loadPolicy();
    }

    public void i_invoke_drift_specifying_the_policy_using_the_rest_service_with_multiple_drift_variables() {
        DriftData driftData = TestDataUtil.createRandomDriftVariables(0, 40);
        invokeDrift(driftData);
    }

    public void i_invoke_drift_specifying_the_policy_using_the_rest_service_with_a_single_drift_variable() {
        DriftData driftData = TestDataUtil.createRandomDriftVariable(0, 40);
        invokeDrift(driftData);
    }

    private void invokeDrift(DriftData driftData) {
        DriftDataInput driftDataInput = new DriftDataInput();
        driftDataInput.setInput(driftData);

        String json = getJsonString(driftDataInput);
        // Log the json that will be sent over
        logger.info("JSON sent to drift service: {}", json);
        result = driftDetectionService.invoke(policyIdentifier, json);
    }

    public void i_receive_the_results_of_drift_detection() {
        assertNotNull(result, "The drift detection result was unexpectedly null");
        assertEquals(Status.SUCCESS, result.getStatus(), "Drift was unexpectedly unencountered");
    }

    private void createTestPolicy() {

        PolicyInput policy = new PolicyInput(policyIdentifier);
        PolicyRuleInput rule = new PolicyRuleInput(ShortHand.STANDARD_DEVIATION.shortHand);
        policy.addRule(rule);
        Map<String, Object> configurations = new HashMap<String, Object>();

        // Results in a range of between 10-30, so we'll just make our test data
        // fall within it
        configurations.put(StandardDeviationDriftAlgorithm.MEAN_CONFIGURATION, mean);
        configurations.put(StandardDeviationDriftAlgorithm.STANDARD_DEVIATION_CONFIGURATION, standardDeviation);
        configurations.put(StandardDeviationDriftAlgorithm.ZSCORE_CONFIGURATION, zScore);
        policy.getRules().get(0).setConfiguration(configurations);
        PolicyTestUtil.writePolicyToFile(policy, directory, "policy.json");
    }

    private void loadPolicy() {
        PolicyManager instance = PolicyManager.getInstance();
        instance.loadPolicyConfigurations(directory);
    }

    private String getJsonString(DriftDataInput driftDataInput) {
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writeValueAsString(driftDataInput);
        } catch (JsonProcessingException e) {
            logger.error("Could not convert object to json string", e);
        }
        return json;
    }
}
