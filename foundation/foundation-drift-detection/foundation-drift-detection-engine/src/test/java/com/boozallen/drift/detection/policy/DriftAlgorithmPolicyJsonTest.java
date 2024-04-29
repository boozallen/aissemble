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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boozallen.drift.detection.policy.json.PolicyInput;
import com.boozallen.drift.detection.policy.json.rule.PolicyRuleInput;
import com.boozallen.drift.detection.policy.json.rule.PolicyRuleInputAlgorithm;
import com.boozallen.drift.detection.policy.json.rule.PolicyRuleInputConfiguration;
import com.boozallen.drift.detection.policy.json.rule.PolicyRuleInputTarget;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * {@link DriftAlgorithmPolicyJsonTest} class is just used for when you're
 * tweaking json and want to see the output quickly.
 * 
 * @author Booz Allen Hamilton
 *
 */
public class DriftAlgorithmPolicyJsonTest {

    private static final Logger logger = LoggerFactory.getLogger(DriftAlgorithmPolicyJsonTest.class);

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testDriftAlgorithmPolicyConfigJsonOutput() throws JsonProcessingException {

        // Log an example of using a configured algorithm
        Map<String, Object> configs = new HashMap<String, Object>();
        configs.put("url", "http://my.host/myService/endpoint");

        PolicyRuleInputConfiguration rule = new PolicyRuleInputConfiguration("StandardDeviation", configs);
        createAndLogPolicy("policy with one rule running standard deviation with rest config", Arrays.asList(rule));
    }

    @Test
    public void testDriftAlgorithmPolicyDifferentConfigsJsonOutput() throws JsonProcessingException {

        // Log an example of using a different set of configuration for the
        // algorithm
        Map<String, Object> configs = new HashMap<String, Object>();
        configs.put("standardDeviation", new Double("2.5"));
        configs.put("limit", 1);

        PolicyRuleInput rule = new PolicyRuleInputConfiguration("StandardDeviation", configs);
        createAndLogPolicy("policy with one rule running standard deviation with algorithm config",
                Arrays.asList(rule));
    }

    @Test
    public void testDriftAlgorithmPolicyTargetRuleJsonOutput() throws JsonProcessingException {

        // Log an example using a rule that doesn't have a config param -- just
        // target and algorithm
        String target = "myTargetColumn";
        PolicyRuleInput rule = new PolicyRuleInputTarget("MeanDifference", target);
        createAndLogPolicy("Policy with one rule running mean difference with target config", Arrays.asList(rule));
    }

    @Test
    public void testDriftAlgorithmPolicyAlgorithmJsonOutput() throws JsonProcessingException {
        PolicyRuleInput rule = new PolicyRuleInputAlgorithm("CustomAlgorithm");
        createAndLogPolicy("Policy with one rule running a custom algorithm", Arrays.asList(rule));
    }

    @Test
    public void testDriftAlgorithmPolicyWithTargetAndConfigs() throws JsonProcessingException {
        String target = "myTargetColumn";
        Map<String, Object> configs = new HashMap<String, Object>();
        configs.put("customSetting", "new setting");
        configs.put("clockSkew", 5);
        PolicyRuleInput rule = new PolicyRuleInput("MyDriftAlgorithm", configs, target);
        createAndLogPolicy("Policy with one rule running a custom algorithm with a target and configurations",
                Arrays.asList(rule));
    }

    @Test
    public void testDriftAlgorithmPolicyMultipleRulesConfigJsonOutput() throws JsonProcessingException {

        // Log an example of using more than one rule
        List<PolicyRuleInput> rules = new ArrayList<PolicyRuleInput>();
        String target = "firstTarget";
        PolicyRuleInput firstRule = new PolicyRuleInputTarget("MeanDifference", target);
        rules.add(firstRule);

        target = "secondTarget";
        Map<String, Object> configs = new HashMap<String, Object>();
        configs.put("aConfig", 100.0);
        PolicyRuleInput secondRule = new PolicyRuleInput("CustomDriftAlgorithm", configs, target);
        rules.add(secondRule);

        PolicyRuleInput thirdRule = new PolicyRuleInputAlgorithm("SimpleAlgorithm");
        rules.add(thirdRule);
        createAndLogPolicy("Policy with several rules", rules);
    }

    private void createAndLogPolicy(String description, List<PolicyRuleInput> rules) throws JsonProcessingException {
        PolicyInput policy = new PolicyInput("myPolicyIdentifier", rules);

        String jsonString = mapper.writeValueAsString(policy);
        logger.debug(description + ": {}", jsonString);
    }

}
