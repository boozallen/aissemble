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

import com.boozallen.aissemble.core.policy.configuration.policy.Policy;

/**
 * {@link DataTransformPolicy} class represents the contract for a data
 * transform policy.
 * 
 * @author Booz Allen Hamilton
 * 
 */
public interface DataTransformPolicy extends Policy {

    /**
     * Returns the mediation manager instance for this policy.
     * 
     * @return mediation manager instance
     */
    public BaseMediationManager getMediationManager();

}
