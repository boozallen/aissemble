package com.boozallen.aissemble.data.encryption.policy.json;

/*-
 * #%L
 * aiSSEMBLE Data Encryption::Policy::Java
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.data.encryption.policy.config.EncryptAlgorithm;
import com.boozallen.aissemble.core.policy.configuration.policy.json.PolicyInput;
import com.boozallen.aissemble.core.policy.configuration.policy.json.rule.PolicyRuleInput;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * {@link EncryptionPolicyInput} class represents policy information that will be read in
 * from a JSON file. Used for reading and writing JSON files, but not during
 * normal encryption invocation.
 * 
 * @author Booz Allen Hamilton
 *
 */
public class EncryptionPolicyInput extends PolicyInput {
    @JsonProperty
    private List<String> encryptFields;

    @JsonProperty
    private String encryptPhase;

    @JsonProperty
    private EncryptAlgorithm encryptAlgorithm;

    public EncryptionPolicyInput() {
        super();
    }

    public EncryptionPolicyInput(String identifier) {
        this.identifier = identifier;
    }

    public EncryptionPolicyInput(String identifier, List<PolicyRuleInput> rules) {
        this.identifier = identifier;
        this.rules = rules;
    }

    public List<String> getEncryptFields() {
        return encryptFields;
    }

    public void setEncryptFields(List<String> encryptFields) {
        this.encryptFields = encryptFields;
    }

    public String getEncryptPhase() {
        return encryptPhase;
    }

    public void setEncryptPhase(String encryptPhase) {
        this.encryptPhase = encryptPhase;
    }

    public EncryptAlgorithm getEncryptAlgorithm() {
        return encryptAlgorithm;
    }

    public void setEncryptAlgorithm(EncryptAlgorithm encryptAlgorithm) {
        this.encryptAlgorithm = encryptAlgorithm;
    }
}
