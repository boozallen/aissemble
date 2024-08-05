package com.boozallen.aissemble.configuration.policy;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Configuration::Store
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
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
