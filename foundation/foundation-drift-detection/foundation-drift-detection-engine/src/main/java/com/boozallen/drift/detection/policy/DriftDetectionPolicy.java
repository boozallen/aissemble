package com.boozallen.drift.detection.policy;

/*-
 * #%L
 * Drift Detection::Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.List;

/**
 * {@link DriftDetectionPolicy} class maps a rule or set of rules that will be
 * used to calculate drift on an input. The identifier is passed in during drift
 * service invocation. The drift service uses it to find the matching policy and
 * the algorithms and any configurations that should be used for drift
 * calculation.
 * 
 * @author Booz Allen Hamilton
 *
 */
public interface DriftDetectionPolicy {
    
    public AlertOptions getShouldSendAlert();

    public String getIdentifier();

    public String getDescription();

    public List<PolicyRule> getRules();

}
