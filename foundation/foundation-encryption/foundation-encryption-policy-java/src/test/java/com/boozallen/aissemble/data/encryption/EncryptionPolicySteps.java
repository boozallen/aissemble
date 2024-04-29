package com.boozallen.aissemble.data.encryption;

/*-
 * #%L
 * aiSSEMBLE Data Encryption::Policy::Java
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.data.encryption.policy.EncryptionPolicy;
import com.boozallen.aissemble.data.encryption.policy.EncryptionPolicyManager;
import com.boozallen.aissemble.data.encryption.policy.config.EncryptAlgorithm;
import com.boozallen.aissemble.core.policy.configuration.policy.Policy;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EncryptionPolicySteps {
    private EncryptionPolicyManager encryptionPolicyManager = EncryptionPolicyManager.getInstance();

    @Given("a json file with a policy that contains a list of encrypt fields")
    public void a_json_file_with_a_policy_that_contains_a_list_of_encrypt_fields() {
        // Nothing to do here. Static file includes a list of fields.
    }

    @When("the policy is loaded from the file")
    public void the_policy_is_loaded_from_the_file() {
        // Load the files from the configured path
        Map<String, Policy> policies = encryptionPolicyManager.getPolicies();
        assertEquals("Wrong number of policies.", 1, policies.size());
    }

    @Then("the policy is available for field encryption")
    public void the_policy_is_available_for_field_encryption() {
        Map<String, EncryptionPolicy> policies = encryptionPolicyManager.getEncryptPolicies();
        assertTrue(policies.containsKey("ExamplePolicy"));
        for(EncryptionPolicy encryptionPolicy: policies.values()) {
            List<String> encryptFields = encryptionPolicy.getEncryptFields();
            assertEquals("Wrong number of encrypt field.", 1, encryptFields.size());
            assertEquals("Wrong data delivery phase.", "INGEST", encryptionPolicy.getEncryptPhase().toUpperCase());
        }
    }

    @Given("a json file with a policy that specifies an algorithm")
    public void a_json_file_with_a_policy_that_specifies_an_algorithm() {
        // Nothing to do here.  Static file includes the algorithm.
    }

    @Then("the policy is available with the algorithm")
    public void the_policy_is_available_with_the_algorithm() {
        Map<String, EncryptionPolicy> policies = encryptionPolicyManager.getEncryptPolicies();
        assertTrue(policies.containsKey("ExamplePolicy"));
        for(EncryptionPolicy encryptionPolicy: policies.values()) {
            assertEquals("Wrong encrypt algorithm.", EncryptAlgorithm.AES_ENCRYPT, encryptionPolicy.getEncryptAlgorithm());
        }
    }
}
