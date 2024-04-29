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

import java.util.ArrayList;
import java.util.List;

import com.boozallen.aissemble.core.policy.configuration.configuredrule.ConfiguredRule;

/**
 * {@link DefaultPolicy} class is used as a default implementation for
 * {@link Policy} with generic implementation of the methods.
 * 
 * @author Booz Allen Hamilton
 *
 */
public class DefaultPolicy implements Policy {

    private static final AlertOptions DEFAULT = AlertOptions.ON_DETECTION;

    private String identifier;

    private String description;

    private AlertOptions alertOptions = DEFAULT;

    private Target target;

    private List<ConfiguredRule> rules = new ArrayList<>();

    public DefaultPolicy() {
        super();
    }

    public DefaultPolicy(String identifier) {
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
    public List<ConfiguredRule> getRules() {
        return rules;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRules(List<ConfiguredRule> rules) {
        this.rules = rules;
    }

    public void addRule(ConfiguredRule rule) {
        if (rules == null) {
            rules = new ArrayList<>();
        }
        rules.add(rule);
    }

    @Override
    public String toString() {
        return identifier + ": " + description;
    }

    @Override
    public AlertOptions getAlertOptions() {
        return alertOptions;
    }

    public void setAlertOptions(AlertOptions alertOptions) {
        if (alertOptions != null) {
            this.alertOptions = alertOptions;
        }
    }

    @Override
    public Target getTarget() {
        return this.target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

}
