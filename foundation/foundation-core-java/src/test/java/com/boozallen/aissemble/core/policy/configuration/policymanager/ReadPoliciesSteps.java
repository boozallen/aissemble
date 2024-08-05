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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.aeonbits.owner.KrauseningConfigFactory;
import org.apache.commons.lang3.RandomUtils;

import com.boozallen.aissemble.core.policy.configuration.PolicyConfiguration;
import com.boozallen.aissemble.core.policy.configuration.policy.Policy;
import com.boozallen.aissemble.core.policy.configuration.policy.json.PolicyInput;
import com.boozallen.aissemble.core.policy.configuration.policy.json.rule.PolicyRuleInput;
import com.boozallen.aissemble.core.policy.configuration.util.PolicyTestUtil;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ReadPoliciesSteps {

    private String filePath;

    private PolicyInput policyInput;

    private List<PolicyInput> expectedPolicies = new ArrayList<PolicyInput>();

    private DefaultPolicyManager policyManager = DefaultPolicyManager.getInstance();

    private static PolicyConfiguration configuration = KrauseningConfigFactory.create(PolicyConfiguration.class);

    @After("@readPolicies")
    private void clear() {
        expectedPolicies.clear();
    }

    @Given("a json file with a policy with multiple rules")
    public void a_json_file_with_a_policy_with_multiple_rules() {
        filePath = configuration.getPoliciesLocation();
        String fileName = "multiple-rules.json";
        policyInput = PolicyTestUtil.getRandomPolicy();
        PolicyTestUtil.writePolicyToFile(policyInput, filePath, fileName);
    }

    @Given("a json file with a policy using the deprecated target attribute")
    public void a_json_file_with_a_policy_using_the_deprecated_target_attribute() {
        filePath = configuration.getPoliciesLocation();
        String fileName = "deprecated-target.json";
        policyInput = PolicyTestUtil.getRandomPolicy();
        policyInput.setTargets(null);
        policyInput.setTarget(PolicyTestUtil.getRandomTargets(1).get(0));
        PolicyTestUtil.writePolicyToFile(policyInput, filePath, fileName);
    }

    @Given("multiple json files exist, each with a configured policy")
    public void multiple_json_files_exist_each_with_a_configured_policy() {
        filePath = "./target/multiple-json-files";
        int randomJsonFiles = RandomUtils.nextInt(2, 7);

        // Create some random json file
        for (int i = 1; i <= randomJsonFiles; i++) {
            String fileName = "policy-file-" + i + ".json";
            PolicyInput randomPolicy = PolicyTestUtil.getRandomPolicy();
            PolicyTestUtil.writePolicyToFile(randomPolicy, filePath, fileName);
            expectedPolicies.add(randomPolicy);
        }
    }

    @Given("a policy has been configured without an identifier")
    public void a_policy_has_been_configured_without_an_identifier() {
        policyInput = new PolicyInput();
        PolicyRuleInput rule = new PolicyRuleInput("StandardDeviation");
        policyInput.addRule(rule);

        String fileName = "no-identifier.json";
        filePath = "./target/no-identifier";
        PolicyTestUtil.writePolicyToFile(policyInput, filePath, fileName);
    }

    @Given("a json file with multiple policies")
    public void a_json_file_with_multiple_policies() {
        filePath = "./target/multiple-policies-single-file";
        String fileName = "policies.json";
        int randomPolicies = RandomUtils.nextInt(2, 8);

        // Create some random policies
        for (int i = 1; i <= randomPolicies; i++) {
            PolicyInput randomPolicy = PolicyTestUtil.getRandomPolicy();
            expectedPolicies.add(randomPolicy);
        }

        PolicyTestUtil.writePoliciesToFile(expectedPolicies, filePath, fileName);

    }

    @When("the policy is loaded from the file")
    public void the_policy_is_loaded_from_the_file() {
        readPoliciesFromFilePath();
    }

    @When("the policies are loaded from the files")
    public void the_policies_are_loaded_from_the_files() {
        readPoliciesFromFilePath();
    }

    @When("the policies are loaded from the file")
    public void the_policies_are_loaded_from_the_file() {
        readPoliciesFromFilePath();
    }

    @Then("the policy is not added")
    public void the_policy_is_not_added() {
        Map<String, Policy> policies = policyManager.getPolicies();
        assertTrue("Policy without identifier was unexpectedly added", policies.isEmpty());
    }

    @Then("the policy is available for service invocation")
    public void the_policy_is_available_for_service_invocation() {
        String identifier = policyInput.getIdentifier();
        Map<String, Policy> policies = policyManager.getPolicies();
        assertTrue(policies.containsKey(identifier));
    }

    @Then("the policy has the deprecated target in the new targets attribute")
    public void the_policy_has_the_deprecated_target_in_the_new_targets_attribute() {
        assertEquals("The deprecated input target was not equal to the output policy target", 
            policyInput.getTarget(), policyManager.getPolicy(policyInput.getIdentifier()).getTargets().get(0));
    }


    @Then("all the policies from the multiple json files are available for service invocation")
    public void all_the_policies_from_the_multiple_json_files_are_available_for_service_invocation() {
        verifyMultiplePolicies();
    }

    @Then("all the policies from the file are available for service invocation")
    public void all_the_policies_from_the_file_are_available_for_service_invocation() {
        verifyMultiplePolicies();
    }

    private void verifyMultiplePolicies() {
        Map<String, Policy> actualPolicies = policyManager.getPolicies();
        assertEquals("Did not have expected number of policies", expectedPolicies.size(), actualPolicies.size());

        // Verify all the policies we expect are available
        for (PolicyInput expectedPolicy : expectedPolicies) {
            String expectedIdentifier = expectedPolicy.getIdentifier();
            assertTrue("The loaded policies did not contain an expected policy",
                    actualPolicies.containsKey(expectedIdentifier));
        }
    }

    private void readPoliciesFromFilePath() {

        // Load the files from the configured path
        Map<String, Policy> policies = policyManager.getPolicies();
        policies.clear();
        policyManager.loadPolicyConfigurations(filePath);
    }

}
