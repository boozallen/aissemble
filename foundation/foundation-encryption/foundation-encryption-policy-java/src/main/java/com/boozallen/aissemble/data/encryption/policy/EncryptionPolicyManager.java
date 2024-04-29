package com.boozallen.aissemble.data.encryption.policy;

/*-
 * #%L
 * aiSSEMBLE Data Encryption::Policy::Java
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.data.encryption.EncryptionException;
import com.boozallen.aissemble.data.encryption.policy.json.EncryptionPolicyInput;
import com.boozallen.aissemble.core.policy.configuration.policy.DefaultPolicy;
import com.boozallen.aissemble.core.policy.configuration.policy.Policy;
import com.boozallen.aissemble.core.policy.configuration.policy.json.PolicyInput;
import com.boozallen.aissemble.core.policy.configuration.policymanager.AbstractPolicyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link EncryptionPolicyManager} is that class that is used for loading and configuring
 * policies.
 * 
 * @author Booz Allen Hamilton
 *
 */
public class EncryptionPolicyManager extends AbstractPolicyManager {

    private static final Logger logger = LoggerFactory.getLogger(EncryptionPolicyManager.class);

    private static EncryptionPolicyManager instance;

    private static String POLICY_LOCATION = "POLICY_LOCATION";

    private Map<String, EncryptionPolicy> encryptionPolicies;

    private EncryptionPolicyManager() {
        super();
    }

    public static final EncryptionPolicyManager getInstance() {
        if (instance == null) {
            synchronized (EncryptionPolicyManager.class) {
                instance = new EncryptionPolicyManager();
            }
        }
        return instance;
    }

    /**
     * Gets the encryption specific policies.
     * @return Encryption policies.
     */
    public Map<String, EncryptionPolicy> getEncryptPolicies() {
        if(encryptionPolicies == null) {
            encryptionPolicies = new HashMap<>();
            Map<String, Policy> policies = getPolicies();
            if(policies != null) {
                for (Map.Entry<String, Policy> entry : policies.entrySet()) {
                    String key = entry.getKey();
                    Policy value = entry.getValue();
                    if(value instanceof EncryptionPolicy) {
                        encryptionPolicies.put(key, (EncryptionPolicy) value);
                    }
                }
            }
        }

        return encryptionPolicies;
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
        return EncryptionPolicyInput.class;
    }

    /**
     * Method that allows subclasses to use an extended type for the policy
     * class.
     *
     * @param policyIdentifier the policy id
     * @return Default encryption policy
     */
    @Override
    public DefaultPolicy createPolicy(String policyIdentifier) {
        return new DefaultEncryptionPolicy(policyIdentifier);
    }

    /**
     * Method that allows subclasses to set any additional configurations while
     * reading a policy.
     */
    @Override
    public void setAdditionalConfigurations(Policy policy, PolicyInput input) {

        // Make sure that the policy is the type we're expecting
        if (policy instanceof DefaultEncryptionPolicy && input instanceof EncryptionPolicyInput) {

            // Set the additional configurations
            DefaultEncryptionPolicy encryptionPolicy = (DefaultEncryptionPolicy) policy;
            EncryptionPolicyInput encryptionInput = (EncryptionPolicyInput) input;
            encryptionPolicy.setEncryptPhase(encryptionInput.getEncryptPhase());
            encryptionPolicy.setEncryptFields(encryptionInput.getEncryptFields());
            encryptionPolicy.setEncryptAlgorithm(encryptionInput.getEncryptAlgorithm());
        } else {
            throw new EncryptionException("Policy was not configured for encryption.");
        }
    }
}
