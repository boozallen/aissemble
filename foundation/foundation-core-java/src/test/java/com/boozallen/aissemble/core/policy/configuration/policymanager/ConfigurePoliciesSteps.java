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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import com.boozallen.aissemble.core.policy.configuration.configuredrule.ConfiguredRule;
import com.boozallen.aissemble.core.policy.configuration.policy.ConfiguredTarget;
import com.boozallen.aissemble.core.policy.configuration.policy.Policy;
import com.boozallen.aissemble.core.policy.configuration.policy.Target;
import com.boozallen.aissemble.core.policy.configuration.policy.json.PolicyInput;
import com.boozallen.aissemble.core.policy.configuration.policy.json.rule.PolicyRuleInput;
import com.boozallen.aissemble.core.policy.configuration.util.PolicyTestUtil;

import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ConfigurePoliciesSteps {

    private PolicyRuleInput ruleInput;

    private ConfiguredRule policyRule;

    private PolicyInput policyInput;

    private Map<String, Object> expectedConfigurations;

    private String identifier;

    private DefaultPolicyManager policyManager = DefaultPolicyManager.getInstance();

    @DataTableType
    public Target targetEntry(Map<String, String> entry) {
        return new Target(entry.get("retrieveUrl"), entry.get("type"));
    }

    @Given("a policy has been configured with {int} rules")
    public void a_policy_has_been_configured_with_rule(Integer numberRules) {
        createValidPolicy(numberRules);
    }

    @Given("a policy has been configured with the deprecated target attribute")
    public void a_policy_has_been_configured_with_the_deprecated_target_attribute() {
        createValidPolicy(1);
        policyInput.setTarget(PolicyTestUtil.getRandomTargets(1).get(0));
    }

    @Given("a policy has been configured with {int} targets")
    public void a_policy_has_been_configured_with_targets(Integer numberTargets) {
        createValidPolicy(1);
        policyInput.setTargets(PolicyTestUtil.getRandomTargets(numberTargets));
    }

    @Given("a rule within a policy has been configured without a class name")
    public void a_rule_within_a_policy_has_been_configured_without_a_class_name() {
        identifier = RandomStringUtils.randomAlphabetic(10);
        policyInput = new PolicyInput(identifier);
        policyInput.setRules(PolicyTestUtil.getRandomRules(RandomUtils.nextInt(1, 4)));
        addInvalidRules(policyInput);
    }

    @Given("a policy has no valid rules")
    public void a_policy_has_no_valid_rules() {

        identifier = RandomStringUtils.randomAlphabetic(10);
        policyInput = new PolicyInput(identifier);
        addInvalidRules(policyInput);
    }

    @Given("a valid policy exists")
    public void a_valid_policy_exists() {
        createValidPolicy(RandomUtils.nextInt(1, 4));
    }

    @Given("the policy specifies a target")
    public void the_policy_specifies_a_target(List<Target> targetList) {
        Target expectedTarget = targetList.get(0);
        policyInput.setTargets(Arrays.asList(expectedTarget));
    }

    @Given("a policy exists with the following targets:")
    public void a_policy_exists_with_the_following_targets(List<Target> targetsList) {
        identifier = RandomStringUtils.randomAlphabetic(10);
        policyInput = new PolicyInput(identifier);
        List<Target> expectedTargets = targetsList;
        policyInput.setTargets(expectedTargets);
    }

    @Given("a policy rule that uses the class {string} with the following configurations:")
    public void a_policy_rule_that_uses_the_class_with_the_following_configurations(String className,
            Map<String, Object> configurations) {

        ruleInput = new PolicyRuleInput(className);
        ruleInput.setConfigurations(configurations);
        this.expectedConfigurations = configurations;
    }
    
    @Given("a policy rule specifies the target configurations:")
    public void a_policy_rule_specifies_the_target_configurations(Map<String, Object> targetConfigurations) {
        ruleInput = new PolicyRuleInput("TestClass");
        ruleInput.setTargetConfigurations(targetConfigurations);
        expectedConfigurations = targetConfigurations;
        policyInput.addRule(ruleInput);
    }

    @Given("a valid policy rule exists")
    public void a_valid_policy_rule_exists() {
        ruleInput = new PolicyRuleInput(PolicyTestUtil.getRandomAlgorithm());
    }

    @When("the policy is read in")
    public void the_policy_is_read_in() {
        Map<String, Policy> policies = policyManager.getPolicies();
        policies.clear();
        policyManager.validateAndAddPolicy(policyInput);
    }

    @When("the policy rule is read in")
    public void the_policy_rule_is_read_in() {
        policyRule = policyManager.validateAndConfigureRule(ruleInput, Arrays.asList(new Target()));
    }

    @Then("the policy has {int} corresponding rules")
    public void the_policy_has_corresponding_rules(int expectedNumber) {
        List<ConfiguredRule> actualRules = getActualRules();
        assertEquals("Number of configured rules did not match expected", expectedNumber, actualRules.size());
    }

    @Then("the policy has {int} corresponding targets")
    public void the_policy_has_corresponding_targets(int expectedNumber) {
        List<Target> actualTargets = getActualTargets();
        assertEquals("Number of configured targets did not match expected", expectedNumber, actualTargets.size());
    }

    @Then("the rule is ignored")
    public void the_rule_is_ignored() {
        List<ConfiguredRule> actualRules = getActualRules();
        for (ConfiguredRule actualRule : actualRules) {
            assertTrue("A rule with a blank classname was unexpectedly added",
                    StringUtils.isNotBlank(actualRule.getClassName()));
        }
    }

    @Then("the policy is ignored")
    public void the_policy_is_ignored() {
        Map<String, Policy> policies = policyManager.getPolicies();
        assertFalse("A policy with no valid rules was unexpectedly added", policies.containsKey(identifier));
    }

    @Then("the configurations are available to the rule")
    public void the_configurations_are_available_to_the_class() {
        Map<String, Object> actualConfigurations = policyRule.getConfigurations();
        assertNotNull("Configurations for algorithm were unexpectedly null", actualConfigurations);
        verifyConfigurations(expectedConfigurations, actualConfigurations);
    }

    @Then("the target type is set as {string}")
    public void the_target_type_is_set_as(String expectedType) {
        Target actualTarget = getActualTargets().get(0);
        assertEquals("The target type did not match the expected", expectedType, actualTarget.getType());
    }

    @Then("the target's retrieve url is set as {string}")
    public void the_target_s_retrieve_url_is_set_as(String expectedRetrieveUrl) {
        Target actualTarget = getActualTargets().get(0);
        assertEquals("The target expected retrieve url did not match the expected", expectedRetrieveUrl,
                actualTarget.getRetrieveUrl());
    }

    @Then("the configured targets are available to the rule")
    public void the_configured_targets_are_available_to_the_rule() {
        
        List<ConfiguredRule> actualRules = getActualRules();
        assertEquals("The number of rules was unexpectedly not 1", 1, actualRules.size());
        ConfiguredRule actualRule = actualRules.get(0);
        List<ConfiguredTarget> actualConfiguredTargets = actualRule.getConfiguredTargets();
        assertNotNull("Target configurations for algorithm were unexpectedly null", actualConfiguredTargets);

        for (ConfiguredTarget actualConfiguredTarget: actualConfiguredTargets) {
            verifyConfigurations(expectedConfigurations, actualConfiguredTarget.getTargetConfigurations());
        }
    }
    
    public void verifyConfigurations(Map<String, Object> expectedConfigurations, Map<String, Object> actualConfigurations) {
        assertEquals("The number of configurations found did not match the expected number",
                expectedConfigurations.size(), actualConfigurations.size());
        Set<String> expectedKeys = expectedConfigurations.keySet();
        for (String expectedKey : expectedKeys) {
            assertTrue("Configurations did not contain expected key", actualConfigurations.containsKey(expectedKey));
            Object expectedValue = expectedConfigurations.get(expectedKey);
            Object actualValue = actualConfigurations.get(expectedKey);
            assertEquals("The expected value for the key " + expectedKey + " did not match the actual value",
                    expectedValue, actualValue);
        }
    }

    private List<Target> getActualTargets() {
        Policy actualPolicy = getActualPolicy();
        List<Target> actualTargets = actualPolicy.getTargets();
        assertNotNull("Targets was unexpectedly null", actualTargets);
        return actualTargets;
    }

    private List<ConfiguredRule> getActualRules() {
        Policy actualPolicy = getActualPolicy();
        return actualPolicy.getRules();
    }

    private Policy getActualPolicy() {
        Map<String, Policy> policies = policyManager.getPolicies();
        assertEquals("Number of policies did not match expected", 1, policies.size());

        return policies.get(identifier);
    }

    private void createValidPolicy(int numberRules) {
        identifier = RandomStringUtils.randomAlphabetic(10);
        policyInput = new PolicyInput(identifier);
        policyInput.setRules(PolicyTestUtil.getRandomRules(numberRules));
    }

    private void addInvalidRules(PolicyInput policyInput) {
        int random = RandomUtils.nextInt(1, 4);
        for (int i = 0; i < random; i++) {

            // Add an invalid rule
            Map<String, Object> targetConfigurations = new HashMap<String, Object>();
            targetConfigurations.put("column", "transactions");
            PolicyRuleInput rule = new PolicyRuleInput("", null, targetConfigurations);
            policyInput.addRule(rule);
        }
    }

}
