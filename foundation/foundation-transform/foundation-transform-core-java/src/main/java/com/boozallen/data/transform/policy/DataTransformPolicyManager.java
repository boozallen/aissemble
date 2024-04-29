package com.boozallen.data.transform.policy;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Transform::Java
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;

import org.technologybrewery.mash.BaseMediationManager;
import org.technologybrewery.mash.MediationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boozallen.aissemble.core.policy.configuration.policy.DefaultPolicy;
import com.boozallen.aissemble.core.policy.configuration.policy.Policy;
import com.boozallen.aissemble.core.policy.configuration.policy.json.PolicyInput;
import com.boozallen.aissemble.core.policy.configuration.policy.json.rule.PolicyRuleInput;
import com.boozallen.aissemble.core.policy.configuration.policymanager.AbstractPolicyManager;
import com.boozallen.data.transform.DataTransformException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * {@link DataTransformPolicyManager} class that overrides the methods in the
 * policy manager to add custom configurations for the data transform policies.
 * 
 * @author Booz Allen Hamilton
 *
 */
public class DataTransformPolicyManager extends AbstractPolicyManager {

    private static final Logger logger = LoggerFactory.getLogger(DataTransformPolicyManager.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static DataTransformPolicyManager instance = null;

    /**
     * Returns the policy manager instance.
     * 
     * @return policy manager instance
     */
    public static DataTransformPolicyManager getInstance() {
        if (instance == null) {
            synchronized (DataTransformPolicyManager.class) {
                instance = new DataTransformPolicyManager();
            }
        }

        return instance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefaultPolicy createPolicy(String policyIdentifier) {
        return new DefaultDataTransformPolicy(policyIdentifier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAdditionalConfigurations(Policy policy, PolicyInput input) {
        if (!DefaultDataTransformPolicy.class.isInstance(policy)) {
            throw new DataTransformException("Policy was not configured for data transform");
        }

        DefaultDataTransformPolicy dataTransformPolicy = (DefaultDataTransformPolicy) policy;

        // leveraging mash to handle mediator lookup based on policy rules
        BaseMediationManager mediationManager = dataTransformPolicy.getMediationManager();
        List<MediationConfiguration> mediationConfigurations = createMediationConfigurations(input.getRules());

        for (MediationConfiguration mediationConfiguration : mediationConfigurations) {
            mediationManager.validateAndAddMediator(mediationConfigurations, mediationConfiguration);
        }
    }

    private List<MediationConfiguration> createMediationConfigurations(List<PolicyRuleInput> ruleInputs) {
        List<MediationConfiguration> mediationConfigurations = new ArrayList<>();
        for (PolicyRuleInput ruleInput : ruleInputs) {
            MediationConfiguration mediationConfiguration = createMediationConfiguration(ruleInput);
            mediationConfigurations.add(mediationConfiguration);
        }

        return mediationConfigurations;
    }

    private MediationConfiguration createMediationConfiguration(PolicyRuleInput ruleInput) {
        MediationConfiguration mediationConfiguration = null;
        try {
            // convert rule configuration into mash's mediation configuration
            mediationConfiguration = objectMapper.convertValue(ruleInput.getConfigurations(),
                    MediationConfiguration.class);
            mediationConfiguration.setClassName(ruleInput.getClassName());

            logger.debug("Created mediation configuration: {}", mediationConfiguration);
        } catch (IllegalArgumentException e) {
            throw new DataTransformException("Invalid configurations found in rule", e);
        }

        return mediationConfiguration;
    }

}
