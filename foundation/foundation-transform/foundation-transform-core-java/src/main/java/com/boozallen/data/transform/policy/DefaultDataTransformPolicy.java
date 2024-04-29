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

import org.technologybrewery.mash.BaseMediationManager;

import com.boozallen.aissemble.core.policy.configuration.policy.DefaultPolicy;

/**
 * {@link DefaultDataTransformPolicy} class represents the additional policy
 * configurations that are needed for data transform.
 * 
 * @author Booz Allen Hamilton
 *
 */
public class DefaultDataTransformPolicy extends DefaultPolicy implements DataTransformPolicy {

    private final BaseMediationManager mediationManager = new BaseMediationManager();

    public DefaultDataTransformPolicy() {
        super();
    }

    public DefaultDataTransformPolicy(String policyIdentifier) {
        super(policyIdentifier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BaseMediationManager getMediationManager() {
        return mediationManager;
    }

}
