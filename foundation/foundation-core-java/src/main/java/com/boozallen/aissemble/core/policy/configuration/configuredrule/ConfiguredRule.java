package com.boozallen.aissemble.core.policy.configuration.configuredrule;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boozallen.aissemble.core.policy.configuration.policy.ConfiguredTarget;

/**
 * {@link ConfiguredRule} represents a rule that has been read in by the policy
 * manager. It stores information about the class to be invoked as well as any
 * configurations for the rule or for the policy target. The object that will
 * invoke the rule will be responsible for looking up the related class.
 * 
 * @author Booz Allen Hamilton
 *
 */
public class ConfiguredRule {

    private static final Logger logger = LoggerFactory.getLogger(ConfiguredRule.class);

    private String className;

    private Map<String, Object> configurations;

    private ConfiguredTarget targetConfigurations;

    /**
     * Default constructor.
     */
    public ConfiguredRule() {
        super();
    }

    /**
     * Constructor for the simplest type of rule, with just a class name.
     * 
     * @param className
     */
    public ConfiguredRule(String className) {
        this(className, null, null);
    }

    /**
     * Constructor for a rule, the configurations for that rule's class, and the
     * configured target for retrieving the data.
     * 
     * @param className
     * @param configurations
     * @param targetConfigurations
     */
    public ConfiguredRule(String className, Map<String, Object> configurations, ConfiguredTarget targetConfigurations) {
        this.className = className;
        this.configurations = configurations;
        this.targetConfigurations = targetConfigurations;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return this.className;
    }

    /**
     * Method that sets any configurations for the target. It's up to the
     * invocation engine if they will actually be used, but they should be
     * available to the rule.
     * 
     * @param targetConfigurations
     */
    public void setTargetConfigurations(ConfiguredTarget targetConfigurations) {
        this.targetConfigurations = targetConfigurations;
    }

    /**
     * Returns the target configurations of this rule.
     * 
     * @return target
     */
    public ConfiguredTarget getTargetConfigurations() {
        return this.targetConfigurations;
    }

    /**
     * Holds any rule configurations. It's up to the class and invocation engine
     * if they will actually be used, but they should be available to the rule.
     * 
     * @param configurations
     */
    public void setConfigurations(Map<String, Object> configurations) {
        if (isValidConfigurations(configurations)) {
            this.configurations = configurations;
        } else {
            logger.warn("Invalid configurations were passed in. Skipping...");
        }
    }

    /**
     * Returns the configurations used by this rule.
     * 
     * @return
     */
    public Map<String, Object> getConfigurations() {
        return this.configurations;
    }

    /**
     * Return the configuration value for this key, if there is one.
     * 
     * @param key
     * @return value
     */
    public Object getConfiguration(String key) {
        Object configuration = null;
        if (configurations != null && configurations.containsKey(key)) {
            configuration = configurations.get(key);
        }
        return configuration;
    }

    /**
     * By default, the configurations are valid, but allow rules to override if
     * they need.
     * 
     * @param configurations
     * @return isValidConfigurations
     */
    public boolean isValidConfigurations(Map<String, Object> configurations) {
        return true;
    }

}
