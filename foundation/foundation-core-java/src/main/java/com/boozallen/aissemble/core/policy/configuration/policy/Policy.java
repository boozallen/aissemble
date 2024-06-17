package com.boozallen.aissemble.core.policy.configuration.policy;

/*-
 * #%L
 * Policy-Based Configuration::Policy Manager
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.List;

import com.boozallen.aissemble.core.policy.configuration.configuredrule.ConfiguredRule;

/**
 * {@link Policy} class maps a rule or set of rules. The identifier is passed in
 * during service invocation. The service uses it to find the matching policy
 * and the classes and any configurations that should be used for policy
 * execution.
 * 
 * @author Booz Allen Hamilton
 *
 */
public interface Policy {

    public AlertOptions getAlertOptions();

    public String getIdentifier();

    public String getDescription();
    
    /**
     * This method is deprecated and should not be used. Targets are now represented as
     * a {@link List} of {@link Target}'s instead of a single {@link Target} attribute.
     * @Deprecated this method is replaced by {@link #getTargets()}
     */
    @Deprecated
    public Target getTarget();

    public List<Target> getTargets();

    public List<ConfiguredRule> getRules();

}
