package com.boozallen.aissemble.configuration.policy.json;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Configuration::Store
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.core.policy.configuration.policy.json.PolicyInput;
import com.boozallen.aissemble.core.policy.configuration.policy.json.rule.PolicyRuleInput;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * {@link PropertyRegenerationPolicyInput} class represents policy information that will be read in
 * from a JSON file. Used for reading and writing JSON files, but not during normal policy invocation.
 */
public class PropertyRegenerationPolicyInput extends PolicyInput {

    @JsonProperty("regeneration_method")
    private PolicyRuleInput regenerationMethod;

    public PropertyRegenerationPolicyInput() {}

    /**
     * Constructor that sets the policy identifier.
     * 
     * @param policyIdentifier
     */
    public PropertyRegenerationPolicyInput(String policyIdentifier) {
        super(policyIdentifier);
    }

    public PolicyRuleInput getRegenerationMethod() {
        return this.regenerationMethod;
    }

    public void setRegenerationMethod(PolicyRuleInput regenerationMethod) {
        this.regenerationMethod = regenerationMethod;
    }   
}
