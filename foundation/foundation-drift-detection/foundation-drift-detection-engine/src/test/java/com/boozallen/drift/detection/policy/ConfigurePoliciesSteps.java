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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aeonbits.owner.KrauseningConfigFactory;
import org.apache.commons.lang3.RandomStringUtils;

import com.boozallen.drift.detection.algorithm.AbstractDriftAlgorithm;
import com.boozallen.drift.detection.algorithm.DriftAlgorithm;
import com.boozallen.drift.detection.configuration.DriftDetectionConfiguration;
import com.boozallen.drift.detection.policy.json.PolicyInput;
import com.boozallen.drift.detection.policy.json.rule.PolicyRuleInput;
import com.boozallen.drift.detection.util.PolicyTestUtil;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ConfigurePoliciesSteps {

    private PolicyRuleInput ruleInput;

    private PolicyRule policyRule;

    private PolicyInput policyInput;

    private DriftAlgorithm algorithm;

    private Map<String, Object> expectedConfigurations;

    private PolicyManager policyManager = PolicyManager.getInstance();

    private static DriftDetectionConfiguration configuration = KrauseningConfigFactory
            .create(DriftDetectionConfiguration.class);

    @Given("a policy has been configured with {int} rules")
    public void a_policy_has_been_configured_with_rule(Integer numberRules) {
        policyInput = new PolicyInput(RandomStringUtils.randomAlphabetic(10));
        policyInput.setRules(PolicyTestUtil.getRandomRules(numberRules));
    }

    @Given("the default package is set to {string}")
    public void the_default_package_is_set_to(String expectedPackage) {
        String defaultPackage = configuration.getDefaultAlgorithmPackage();
        assertEquals("The default algorithm package did not match the expected", expectedPackage, defaultPackage);
    }

    @Given("a policy rule that uses the algorithm {string} with the following configurations:")
    public void a_policy_rule_that_uses_the_algorithm_with_the_following_configurations(String algorithm,
            Map<String, Object> configurations) {

        ruleInput = new PolicyRuleInput(algorithm);
        ruleInput.setConfiguration(configurations);
        this.expectedConfigurations = configurations;
    }

    @Given("a policy rule that uses an algorithm with the following unrecognized configurations")
    public void a_policy_rule_that_uses_an_algorithm_with_the_following_unrecognized_configurations(
            Map<String, Object> configurations) {

        // Use one that doesn't extend Abstract
        String algorithm = "com.different.company.algorithm.CustomDriftAlgorithm";
        ruleInput = new PolicyRuleInput(algorithm);
        ruleInput.setConfiguration(configurations);
        ruleInput.setTarget("whatever");
    }

    @Given("the policy rule specifies a target of {string}")
    public void the_policy_rule_specifies_a_target_of(String target) {
        ruleInput.setTarget(target);
    }

    @When("the configured policies reference an algorithm {string} by class name")
    public void the_configured_policies_reference_an_algorithm_by_class_name(String className) {
        algorithm = policyManager.getConfiguredDriftAlgorithm(className, null, null);
    }

    @When("the policy is read in")
    public void the_policy_is_read_in() {
        Map<String, DriftDetectionPolicy> policies = policyManager.getPolicies();
        policies.clear();
        policyManager.validateAndAddPolicy(policyInput);
    }

    @When("the policy rule is read in")
    public void the_policy_rule_is_read_in() {
        policyRule = policyManager.validateAndConfigureRule(ruleInput);
    }

    @Then("the policy has {int} corresponding rules")
    public void the_policy_has_corresponding_rules(int expectedNumber) {
        Map<String, DriftDetectionPolicy> policies = policyManager.getPolicies();
        assertEquals("Number of policies did not match expected", 1, policies.size());
        Set<String> keys = policies.keySet();
        for (String key : keys) {
            DriftDetectionPolicy actualPolicy = policies.get(key);
            List<PolicyRule> actualRules = actualPolicy.getRules();
            assertEquals("Number of configured rules did not match expected", expectedNumber, actualRules.size());
        }
    }

    @Then("the algorithm from {string} is used")
    public void the_algorithm_from_is_used(String expectedClass) {
        assertNotNull("Could not find algorithm matching identifier", algorithm);
        Class actualClass = algorithm.getClass();
        assertEquals("Expected class for the drift algorithm did not match actual", expectedClass,
                actualClass.getName());
    }

    @Then("the configurations are available to the algorithm")
    public void the_configurations_are_available_to_the_algorithm() {
        AbstractDriftAlgorithm abstractAlgorithm = (AbstractDriftAlgorithm) policyRule.getAlgorithm();
        Map<String, Object> actualConfigurations = abstractAlgorithm.getConfigurations();
        assertNotNull("Configurations for algorithm were unexpectedly null", actualConfigurations);
        assertEquals("", expectedConfigurations.size(), actualConfigurations.size());
        Set<String> expectedKeys = expectedConfigurations.keySet();
        for (String expectedKey : expectedKeys) {
            assertTrue("Configurations did not contain expected key", actualConfigurations.containsKey(expectedKey));
            Object expectedValue = expectedConfigurations.get(expectedKey);
            Object actualValue = actualConfigurations.get(expectedKey);
            assertEquals("The expected value for the key " + expectedKey + " did not match the actual value",
                    expectedValue, actualValue);
        }
    }

    @Then("the target is set as {string}")
    public void the_target_is_set_as(String target) {
        AbstractDriftAlgorithm algorithm = (AbstractDriftAlgorithm) policyRule.getAlgorithm();
        assertEquals("Target did not match expected", target, algorithm.getTarget());
    }

    @Then("the unrecognized configurations are ignored")
    public void the_unrecognized_configurations_are_ignored() {

        // Just make sure the algorithm was still instantiated
        DriftAlgorithm driftAlgorithm = policyRule.getAlgorithm();
        assertNotNull("The drift algorithm was unexpected null", driftAlgorithm);
    }

}
