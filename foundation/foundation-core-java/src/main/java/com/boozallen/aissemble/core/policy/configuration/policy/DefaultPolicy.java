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
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boozallen.aissemble.core.policy.configuration.configuredrule.ConfiguredRule;

/**
 * {@link DefaultPolicy} class is used as a default implementation for
 * {@link Policy} with generic implementation of the methods.
 * 
 * @author Booz Allen Hamilton
 *
 */
public class DefaultPolicy implements Policy {

    private static final Logger logger = LoggerFactory.getLogger(DefaultPolicy.class);

    private static final AlertOptions DEFAULT = AlertOptions.ON_DETECTION;

    private String identifier;

    private String description;

    private AlertOptions alertOptions = DEFAULT;

    private List<Target> targets = new ArrayList<>();

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
        logger.warn("Detected use of deprecated method 'getTarget()'. Existing " + 
                    "method calls should be moved to the new method 'getTargets()'.");
        return getTargets().isEmpty() ? null : getTargets().get(0);
    }

    /**
     * This method is deprecated and should not be used. Targets are now represented as
     * a {@link List} of {@link Target}'s instead of a single {@link Target} attribute.
     * @Deprecated this method is replaced by {@link #setTargets()}
     */
    @Deprecated
    public void setTarget(Target target) {
        logger.warn("Detected use of deprecated method 'setTarget()'. Existing " + 
                    "method calls should be moved to the new method 'setTargets()'.");
        setTargets(Arrays.asList(target));
    }

    @Override
    public List<Target> getTargets() {
        return this.targets;
    }

    public void setTargets(List<Target> targets) {
        this.targets = targets;
    }

    public void addTarget(Target target) {
        if (this.targets == null) {
            this.targets = new ArrayList<>();
        }
        this.targets.add(target);
    }
}
