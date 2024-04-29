package com.boozallen.aissemble.core.policy.configuration.policy.json.rule;

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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * {@link PolicyRuleInput} class represents policy rule data that will be read
 * in from a JSON file.
 * 
 * @author Booz Allen Hamilton
 *
 */
@JsonInclude(Include.NON_NULL)
public class PolicyRuleInput {

    /**
     * The className that should be used with this rule must be specified for
     * the policy rule to be used.
     */
    @JsonProperty
    private String className;

    /**
     * The configuration used for the rule.
     */
    @JsonProperty
    private Map<String, Object> configurations;

    /**
     * Any configurations for the target set of data that is needed by this
     * rule.
     */
    @JsonProperty
    private Map<String, Object> targetConfigurations;

    public PolicyRuleInput() {
        super();
    }

    public PolicyRuleInput(String className) {
        this.className = className;
    }

    public PolicyRuleInput(String className, Map<String, Object> configurations,
            Map<String, Object> targetConfigurations) {
        this.className = className;
        this.configurations = configurations;
        this.targetConfigurations = targetConfigurations;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Map<String, Object> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(Map<String, Object> configurations) {
        this.configurations = configurations;
    }

    public Map<String, Object> getTargetConfigurations() {
        return targetConfigurations;
    }

    public void setTargetConfigurations(Map<String, Object> target) {
        this.targetConfigurations = target;
    }

}
