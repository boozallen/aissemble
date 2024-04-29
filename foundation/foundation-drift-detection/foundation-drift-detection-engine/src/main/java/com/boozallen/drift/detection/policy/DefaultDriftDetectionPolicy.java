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

import java.util.ArrayList;
import java.util.List;

public class DefaultDriftDetectionPolicy implements DriftDetectionPolicy {

    private static final AlertOptions DEFAULT = AlertOptions.ALWAYS;

    private String identifier;

    private String description;

    private AlertOptions shouldSendAlert = DEFAULT;

    private List<PolicyRule> rules = new ArrayList<PolicyRule>();

    public DefaultDriftDetectionPolicy() {
        super();
    }

    public DefaultDriftDetectionPolicy(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<PolicyRule> getRules() {
        return rules;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRules(List<PolicyRule> rules) {
        this.rules = rules;
    }

    public void addRule(PolicyRule rule) {
        if (rules == null) {
            rules = new ArrayList<PolicyRule>();
        }
        rules.add(rule);
    }

    @Override
    public String toString() {
        return identifier + ": " + description;
    }

    @Override
    public AlertOptions getShouldSendAlert() {
        return shouldSendAlert;
    }

    public void setShouldSendAlert(AlertOptions shouldSendAlert) {
        if (shouldSendAlert != null) {
            this.shouldSendAlert = shouldSendAlert;
        }
    }

}
