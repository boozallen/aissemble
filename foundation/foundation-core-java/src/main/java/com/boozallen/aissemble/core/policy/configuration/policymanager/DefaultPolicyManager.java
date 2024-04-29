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

/**
 * {@link DefaultPolicyManager} represents the singleton policy manager that can
 * reuse the methods laid out by the {@link AbstractPolicyManager}.
 * 
 * @author Booz Allen Hamilton
 *
 */
public class DefaultPolicyManager extends AbstractPolicyManager {

    private static DefaultPolicyManager instance = null;

    private DefaultPolicyManager() {
        super();
    }

    public static DefaultPolicyManager getInstance() {
        if (instance == null) {
            synchronized (DefaultPolicyManager.class) {
                instance = new DefaultPolicyManager();
            }
        }
        return instance;
    }

}
