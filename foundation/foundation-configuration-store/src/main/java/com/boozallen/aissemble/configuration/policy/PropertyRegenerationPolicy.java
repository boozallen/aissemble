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

import java.util.List;

import com.boozallen.aissemble.configuration.policy.regeneration.PropertyRegenerationRule;
import com.boozallen.aissemble.configuration.policy.regeneration.PropertyRegenerationStrategy;
import com.boozallen.aissemble.configuration.store.PropertyKey;
import com.boozallen.aissemble.core.policy.configuration.policy.Policy;

/**
 * Interface used defining policies for determining when to regenerate a given {@link Property}.
 */
public interface PropertyRegenerationPolicy extends Policy {
    
    public PropertyRegenerationStrategy getRegenerationStrategy();

    public List<PropertyRegenerationRule> getPropertyRegenerationRules();

    public List<PropertyKey> getTargetPropertyKeys();
}
