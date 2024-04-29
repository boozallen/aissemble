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

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * {@link ConfiguredTarget} contains the target information with any
 * configurations needed by the rule.
 * 
 * @author Booz Allen Hamilton
 *
 */
public class ConfiguredTarget extends Target {

    @JsonProperty("target_configurations")
    private Map<String, Object> targetConfigurations;

    public ConfiguredTarget(Target target, Map<String, Object> targetConfigurations) {
        if (target != null) {
            setRetrieveUrl(target.getRetrieveUrl());
            setType(target.getType());
        }
        this.targetConfigurations = targetConfigurations;
    }

    public Map<String, Object> getTargetConfigurations() {
        return targetConfigurations;
    }

    public void setTargetConfigurations(Map<String, Object> targetConfigurations) {
        this.targetConfigurations = targetConfigurations;
    }

}
