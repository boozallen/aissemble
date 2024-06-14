package com.boozallen.aissemble.core.policy.configuration.policymanager;

/*-
 * #%L
 * Policy-Based Configuration::Policy Manager
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.core.policy.configuration.policy.AlertOptions;
import com.boozallen.aissemble.core.policy.configuration.policy.Target;
import com.boozallen.aissemble.core.policy.configuration.policy.json.PolicyInput;
import com.boozallen.aissemble.core.policy.configuration.policy.json.rule.PolicyRuleInput;
import com.boozallen.aissemble.core.policy.configuration.policy.result.PolicyInvocationResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * {@link PolicyJsonTest} class is just used for when you're tweaking json and
 * want to see the output quickly.
 *
 * @author Booz Allen Hamilton
 */
public class PolicyJsonTest {

    private static final Logger logger = LoggerFactory.getLogger(PolicyJsonTest.class);

    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setupObjectMapper() {
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void testPolicyRequiredJsonOutput() throws JsonProcessingException {

        PolicyInput policy = new PolicyInput("myPolicyIdentifier");

        PolicyRuleInput rule = new PolicyRuleInput("com.boozallen.Detect");
        policy.addRule(rule);

        String jsonString = mapper.writeValueAsString(policy);

        logger.debug("basic policy with one rule: {}", jsonString);
    }

    @Test
    public void testPolicyRuleConfigJsonOutput() throws JsonProcessingException {

        // Log an example of using a configured algorithm
        Map<String, Object> configs = new HashMap<String, Object>();
        configs.put("url", "http://my.host/myService/endpoint");

        PolicyRuleInput rule = new PolicyRuleInput("com.it.MyRestInvokerClass", configs, null);
        createAndLogPolicy("policy with one rule running a class with rest config", Arrays.asList(rule));
    }

    @Test
    public void testPolicyAllFieldsJsonOutput() throws JsonProcessingException {

        PolicyInput policy = new PolicyInput("myPolicyIdentifier");
        policy.setTargets(Arrays.asList(new Target("http://retrieve.com", "rest")));
        policy.setShouldSendAlert(AlertOptions.NEVER);

        PolicyRuleInput rule = new PolicyRuleInput("com.boozallen.DoSomething");
        policy.addRule(rule);

        String jsonString = mapper.writeValueAsString(policy);

        logger.debug("policy with target, alert, and rules: {}", jsonString);
    }

    @Test
    public void testPolicyDifferentConfigsJsonOutput() throws JsonProcessingException {

        // Log an example of using a different set of configuration for the
        // algorithm
        Map<String, Object> configs = new HashMap<String, Object>();
        configs.put("standardDeviation", Double.valueOf("2.5"));
        configs.put("limit", 1);

        PolicyRuleInput rule = new PolicyRuleInput("com.algorithm.MyAlgorithm", configs, null);
        createAndLogPolicy("policy with one configured rule", Arrays.asList(rule));
    }

    @Test
    public void testPolicyRuleTargetConfigurationsJsonOutput() throws JsonProcessingException {

        // Log an example using a rule that doesn't have a config param -- just
        // targetConfigs and class
        Map<String, Object> targetConfigurations = new HashMap<String, Object>();
        targetConfigurations.put("queryCount", 10);
        PolicyRuleInput rule = new PolicyRuleInput("MeanDifference", null, targetConfigurations);
        createAndLogPolicy("Policy with one rule running mean difference with target config", Arrays.asList(rule));
    }

    @Test
    public void testPolicyClassnameJsonOutput() throws JsonProcessingException {
        PolicyRuleInput rule = new PolicyRuleInput("CustomAlgorithm");
        createAndLogPolicy("Policy with one rule running a custom algorithm", Arrays.asList(rule));
    }

    @Test
    public void testPolicyRuleWithTargetAndConfigs() throws JsonProcessingException {
        Map<String, Object> targetConfigs = new HashMap<String, Object>();
        targetConfigs.put("queryParam1", "myData");

        Map<String, Object> configs = new HashMap<String, Object>();
        configs.put("customSetting", "new setting");
        configs.put("clockSkew", 5);
        PolicyRuleInput rule = new PolicyRuleInput("MyDriftAlgorithm", configs, targetConfigs);
        createAndLogPolicy(
                "Policy with one rule running a custom algorithm with target configs and rule configurations",
                Arrays.asList(rule));
    }

    @Test
    public void testPolicyMultipleRulesConfigJsonOutput() throws JsonProcessingException {

        // Log an example of using more than one rule
        List<PolicyRuleInput> rules = new ArrayList<PolicyRuleInput>();
        Map<String, Object> targetConfigs = new HashMap<String, Object>();
        targetConfigs.put("column", "myColumn");
        PolicyRuleInput firstRule = new PolicyRuleInput("MeanDifference", null, targetConfigs);
        rules.add(firstRule);

        Map<String, Object> configs = new HashMap<String, Object>();
        configs.put("aConfig", 100.0);
        PolicyRuleInput secondRule = new PolicyRuleInput("CustomClass", configs, null);
        rules.add(secondRule);

        PolicyRuleInput thirdRule = new PolicyRuleInput("SimpleAlgorithm");
        rules.add(thirdRule);
        createAndLogPolicy("Policy with several rules", rules);
    }

    @Test
    public void testPolicyMultipleTargetsJsonOutput() throws JsonProcessingException {
        PolicyInput policy = new PolicyInput("myPolicyIdentifier");
        
        List<Target> targets = new ArrayList<>();
        targets.add(new Target("http://retrieve1.com", "rest"));
        targets.add(new Target("http://retrieve2.com", "rest"));
        targets.add(new Target("http://retrieve3.com", "rest"));
        policy.setTargets(targets);

        String jsonString = mapper.writeValueAsString(policy);
        logger.debug("policy with multiple targets: {}", jsonString);
        assertTrue(jsonString.contains("\"targets\":"));
        assertFalse(jsonString.contains("\"target\":"));
    }

    @Test
    public void testPolicyDeprecatedTargetJsonOutput() throws JsonProcessingException {
        PolicyInput policy = new PolicyInput("myPolicyIdentifier");
    
        policy.setTarget(new Target("http://retrieve1.com", "rest"));

        String jsonString = mapper.writeValueAsString(policy);
        logger.debug("policy with deprecated target: {}", jsonString);
        assertTrue(jsonString.contains("\"target\":"));
        assertFalse(jsonString.contains("\"targets\":"));
    }

    @Test
    public void testPolicyResultJsonOutput() throws JsonProcessingException {
        PolicyInvocationResult result = new PolicyInvocationResult();
        result.setPolicyDescription("My failed policy");
        result.setPolicyName("MyPolicy");
        result.setTimestamp(Instant.now());
        String jsonString = mapper.writeValueAsString(result);

        logger.debug("json for policy result example: {}", jsonString);
    }

    private void createAndLogPolicy(String description, List<PolicyRuleInput> rules) throws JsonProcessingException {
        PolicyInput policy = new PolicyInput("myPolicyIdentifier", rules);
        policy.setDescription("Description of this policy");

        String jsonString = mapper.writeValueAsString(policy);
        logger.debug(description + ": {}", jsonString);
    }

}
