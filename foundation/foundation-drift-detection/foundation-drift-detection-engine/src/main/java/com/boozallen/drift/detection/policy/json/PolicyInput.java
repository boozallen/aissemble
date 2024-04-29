package com.boozallen.drift.detection.policy.json;

/*-
 * #%L
 * Drift Detection::Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;

import com.boozallen.drift.detection.policy.AlertOptions;
import com.boozallen.drift.detection.policy.json.rule.PolicyRuleInput;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * {@link PolicyInput} class represents policy information that will be read in
 * from a JSON file. Used for reading and writing JSON files, but not during
 * normal drift invocation.
 * 
 * @author Booz Allen Hamilton
 *
 */
public class PolicyInput {

    /**
     * The identifier used by the drift service to look up the policy.
     */
    @JsonProperty
    protected String identifier;

    /**
     * The optional configuration for whether alerts should be sent or not
     */
    @JsonProperty
    protected AlertOptions shouldSendAlert;

    /**
     * The rules for detecting drift for this policy.
     */
    @JsonProperty
    protected List<PolicyRuleInput> rules = new ArrayList<PolicyRuleInput>();

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

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public List<PolicyRuleInput> getRules() {
        return rules;
    }

    public void setRules(List<PolicyRuleInput> rules) {
        this.rules = rules;
    }

    public void addRule(PolicyRuleInput rule) {
        if (rules == null) {
            rules = new ArrayList<PolicyRuleInput>();
        }
        rules.add(rule);
    }

    public AlertOptions getShouldSendAlert() {
        return shouldSendAlert;
    }

    public void setShouldSendAlert(AlertOptions shouldSendAlert) {
        this.shouldSendAlert = shouldSendAlert;
    }

}
