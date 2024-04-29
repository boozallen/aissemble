package com.boozallen.aissemble.core.policy.configuration.policy.json;

/*-
 * #%L
 * Policy-Based Configuration::Policy Manager
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;

import com.boozallen.aissemble.core.policy.configuration.policy.AlertOptions;
import com.boozallen.aissemble.core.policy.configuration.policy.Target;
import com.boozallen.aissemble.core.policy.configuration.policy.json.rule.PolicyRuleInput;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * {@link PolicyInput} class represents policy information that will be read in
 * from a JSON file. Used for reading and writing JSON files, but not during
 * normal policy invocation.
 * 
 * @author Booz Allen Hamilton
 *
 */
@JsonInclude(Include.NON_NULL)
public class PolicyInput {

    /**
     * The identifier used by the service to look up the policy.
     */
    @JsonProperty
    protected String identifier;

    /**
     * The description of the policy.
     */
    @JsonProperty
    protected String description;

    /**
     * The target this policy will be invoked on.
     */
    @JsonProperty
    protected Target target;

    /**
     * The optional configuration for whether alerts should be sent or not
     */
    @JsonProperty
    protected AlertOptions shouldSendAlert;

    /**
     * The rules for this policy.
     */
    @JsonProperty
    protected List<PolicyRuleInput> rules = new ArrayList<>();

    public PolicyInput() {
        super();
    }

    public PolicyInput(String identifier) {
        this.identifier = identifier;
    }

    public PolicyInput(String identifier, List<PolicyRuleInput> rules) {
        this.identifier = identifier;
        this.rules = rules;
    }

    public PolicyInput(String identifier, Target target, List<PolicyRuleInput> rules) {
        this.identifier = identifier;
        this.target = target;
        this.rules = rules;
    }

    public PolicyInput(String identifier, Target target, AlertOptions alertOptions, List<PolicyRuleInput> rules) {
        this.identifier = identifier;
        this.target = target;
        this.rules = rules;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PolicyRuleInput> getRules() {
        return rules;
    }

    public void setRules(List<PolicyRuleInput> rules) {
        this.rules = rules;
    }

    public void addRule(PolicyRuleInput rule) {
        if (rules == null) {
            rules = new ArrayList<>();
        }
        rules.add(rule);
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public AlertOptions getShouldSendAlert() {
        return shouldSendAlert;
    }

    public void setShouldSendAlert(AlertOptions shouldSendAlert) {
        this.shouldSendAlert = shouldSendAlert;
    }
}
