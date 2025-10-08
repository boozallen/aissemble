package com.boozallen.aissemble.configuration.policy;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.boozallen.aissemble.configuration.policy.regeneration.PropertyRegenerationRule;
import com.boozallen.aissemble.configuration.policy.regeneration.PropertyRegenerationStrategy;
import com.boozallen.aissemble.configuration.store.PropertyKey;
import com.boozallen.aissemble.core.policy.configuration.policy.DefaultPolicy;

/**
 * {@link DefaultPropertyRegenerationPolicy} class is used as a default implementation for
 * {@link PropertyRegenerationPolicy} with generic implementation of the methods.
 */
public class DefaultPropertyRegenerationPolicy extends DefaultPolicy implements PropertyRegenerationPolicy {

    private PropertyRegenerationStrategy regenerationStrategy;

    private List<PropertyRegenerationRule> regenerationRules = new ArrayList<>();

    private List<PropertyKey> targetPropertyKeys = new ArrayList<>();

    /**
     * Default constructor.
     */
    public DefaultPropertyRegenerationPolicy() {
        super();
    }

    /**
     * Constructor that sets the policy identifier.
     * 
     * @param policyIdentifier
     */
    public DefaultPropertyRegenerationPolicy(String policyIdentifier) {
        super(policyIdentifier);
    }

    @Override
    public PropertyRegenerationStrategy getRegenerationStrategy() {
        return this.regenerationStrategy;
    }

    public void setRegenerationStrategy(PropertyRegenerationStrategy regenerationStrategy) {
        this.regenerationStrategy = regenerationStrategy;
    }

    @Override
    public List<PropertyRegenerationRule> getPropertyRegenerationRules() {
        return this.regenerationRules;
    }

    public void setPropertyRegenerationRules(List<PropertyRegenerationRule> regenerationRules) {
        this.regenerationRules = regenerationRules; 
    }

    @Override
    public List<PropertyKey> getTargetPropertyKeys() {
        return this.targetPropertyKeys;
    }

    public void setTargetPropertyKeys(List<PropertyKey> targetPropertyKeys) {
        this.targetPropertyKeys = targetPropertyKeys;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PropertyRegenerationPolicy)) {
            return false;
        }

        // Just want to compare the policy identifiers
        PropertyRegenerationPolicy policy = (PropertyRegenerationPolicy) o;
        return Objects.equals(this.getIdentifier(), policy.getIdentifier());
    }

    @Override
    public int hashCode() {
        return this.getIdentifier().hashCode();
    }
}
