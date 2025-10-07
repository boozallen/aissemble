package com.boozallen.aissemble.configuration.policy.json;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Configuration::Store
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
