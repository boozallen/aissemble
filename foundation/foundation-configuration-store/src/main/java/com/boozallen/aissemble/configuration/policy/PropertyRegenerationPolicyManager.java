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

import com.boozallen.aissemble.configuration.policy.json.PropertyRegenerationPolicyInput;
import com.boozallen.aissemble.configuration.policy.regeneration.PropertyRegenerationRule;
import com.boozallen.aissemble.configuration.policy.regeneration.PropertyRegenerationStrategy;
import com.boozallen.aissemble.configuration.policy.exception.PropertyRegenerationPolicyException;
import com.boozallen.aissemble.configuration.store.PropertyKey;
import com.boozallen.aissemble.core.policy.configuration.policy.DefaultPolicy;
import com.boozallen.aissemble.core.policy.configuration.policy.Policy;
import com.boozallen.aissemble.core.policy.configuration.policy.Target;
import com.boozallen.aissemble.core.policy.configuration.policy.json.PolicyInput;
import com.boozallen.aissemble.core.policy.configuration.policy.json.rule.PolicyRuleInput;
import com.boozallen.aissemble.core.policy.configuration.policymanager.AbstractPolicyManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Class used for loading and configuring policies for determining when to regenerate a given {@link Property}.
 */
public class PropertyRegenerationPolicyManager extends AbstractPolicyManager {

    private static final String TARGETS_REGEX = "/([^/]+)/([^/]+)";

    /**
     * Gets the property regeneration specific policies.
     * @return map of policies
     */
    public Set<PropertyRegenerationPolicy> getPropertyRegenerationPolicies() {
        Set<PropertyRegenerationPolicy> propertyRegenerationPolicies = new HashSet<>();
        Map<String, Policy> policies = super.getPolicies();
        if(policies != null) {
            for (Map.Entry<String, Policy> entry : policies.entrySet()) {
                Policy value = entry.getValue();
                if(value instanceof PropertyRegenerationPolicy) {
                    propertyRegenerationPolicies.add((PropertyRegenerationPolicy) value);
                }
            }
        }

        return propertyRegenerationPolicies;
    }

    /**
     * Method that allows subclasses to override the type reference with a
     * subclass.
     * 
     * @return
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Class getDeserializationClass() {
        return PropertyRegenerationPolicyInput.class;
    }

    /**
     * Method that allows subclasses to use an extended type for the policy
     * class.
     * 
     * @param identifier
     * @return {@link DefaultPropertyRegenerationPolicy} instance
     */
    @Override
    public DefaultPolicy createPolicy(String policyIdentifier) {
        return new DefaultPropertyRegenerationPolicy(policyIdentifier);
    }

    /**
     * Method that allows subclasses to set any additional configurations while
     * reading a policy.
     * 
     * @param identifier Policy object to configure
     * @param identifier Input JSON policy object to get configurations from
     */
    @Override
    public void setAdditionalConfigurations(Policy policy, PolicyInput input) {
        
        // Make sure that the policy is the type we're expecting
        if (policy instanceof DefaultPropertyRegenerationPolicy && input instanceof PropertyRegenerationPolicyInput) {
            
            // Set the additional configurations
            DefaultPropertyRegenerationPolicy regenerationPolicy = (DefaultPropertyRegenerationPolicy) policy;
            PropertyRegenerationPolicyInput regenerationPolicyInput = (PropertyRegenerationPolicyInput) input;

            regenerationPolicy.setTargetPropertyKeys(convertTargetsToPropertyKeys(regenerationPolicyInput.getAnyTargets()));
            regenerationPolicy.setPropertyRegenerationRules(convertInputRulesToPropertyRegenerationRules(regenerationPolicyInput.getRules()));
            regenerationPolicy.setRegenerationStrategy(convertInputRuleToPropertyRegenerationStrategy(regenerationPolicyInput.getRegenerationMethod()));

        } else {
            throw new PropertyRegenerationPolicyException("Policy was not configured for Property Regeneration Policy.");
        }
    }

    /**
     * Method for validating and converting {@link Target}'s to their respective {@link PropertyKey}'s.
     */
    private List<PropertyKey> convertTargetsToPropertyKeys(List<Target> targets) {
        if (targets == null || targets.isEmpty()) {
            throw new PropertyRegenerationPolicyException("'targets' is a required field for a Property Regeneration Policy");
        }

        List<PropertyKey> propertyKeys = new ArrayList<>();

        // Iterate through each target and convert it to a property key
        for (Target target: targets) {
            String targetInputUrl = target.getRetrieveUrl();

            if (StringUtils.isBlank(targetInputUrl)) {
                throw new PropertyRegenerationPolicyException("Target's 'retrieve_url' is a required field for a Property Regeneration Policy");
            }

            // compare the target against our regex to ensure it's in the right format
            Pattern pattern = Pattern.compile(TARGETS_REGEX);
            Matcher matcher = pattern.matcher(targetInputUrl);

            if (matcher.matches()) {
                // add the property key
                propertyKeys.add(new PropertyKey(matcher.group(1), matcher.group(2)));
            }
            else {
                throw new PropertyRegenerationPolicyException("Target's 'retrieve_url' must be in '/group_name/property_name` format " + 
                                                                "for a Property Regeneration Policy");
            }
        }

        return propertyKeys;
    }

    /**
     * Method for validating and converting {@link PolicyRuleInput}'s rules to their respective {@link PropertyRegenerationRule}'s.
     */
    private List<PropertyRegenerationRule> convertInputRulesToPropertyRegenerationRules(List<PolicyRuleInput> inputRules) {
        if (inputRules == null || inputRules.isEmpty()) {
            throw new PropertyRegenerationPolicyException("'rules' is a required field for a Property Regeneration Policy");
        }

        List<PropertyRegenerationRule> regenerationRules = new ArrayList<>();

        for (PolicyRuleInput inputRule: inputRules) {
            String className = inputRule.getClassName();
            Map<String, Object> configurations = inputRule.getConfigurations();

            if (StringUtils.isBlank(className)) {
                throw new PropertyRegenerationPolicyException("Rule's 'className' is a required field for a Property Regeneration Policy");
            }

            // TODO: instantiate the class from the className
            regenerationRules.add(new PropertyRegenerationRule());
        }

        return regenerationRules;
    }

    /**
     * Method for validating and converting a {@link PolicyRuleInput} rule to its respective {@link PropertyRegenerationStrategy}.
     */
    private PropertyRegenerationStrategy convertInputRuleToPropertyRegenerationStrategy(PolicyRuleInput inputRule) {
        if (inputRule == null) {
            throw new PropertyRegenerationPolicyException("'regeneration_method' is a required field for a Property Regeneration Policy");
        }

        String className = inputRule.getClassName();
        Map<String, Object> configurations = inputRule.getConfigurations();

        if (StringUtils.isBlank(className)) {
            throw new PropertyRegenerationPolicyException("Regeneration Method's 'className' is a required field for a Property Regeneration Policy");
        }

        // TODO: instantiate the class from the className
        return new PropertyRegenerationStrategy();
    }
}
