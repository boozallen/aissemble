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
    
    public Target getTarget();

    public List<ConfiguredRule> getRules();

}
