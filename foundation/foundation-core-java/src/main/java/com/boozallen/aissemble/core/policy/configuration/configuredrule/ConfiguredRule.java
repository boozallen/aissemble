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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private List<ConfiguredTarget> configuredTargets = new ArrayList<>();

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
     * configured targets for retrieving the data.
     * 
     * @param className
     * @param configurations
     * @param configuredTargets
     */
    public ConfiguredRule(String className, Map<String, Object> configurations, List<ConfiguredTarget> configuredTargets) {
        this.className = className;
        this.configurations = configurations;
        this.configuredTargets = configuredTargets;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return this.className;
    }

    /**
     * This method is deprecated and should not be used. Configured Targets are now represented as a
     * {@link List} of {@link ConfiguredTarget}'s instead of a single {@link ConfiguredTarget} attribute.
     * @Deprecated this method is replaced by {@link #setConfiguredTargets()}
     */
    @Deprecated
    public void setTargetConfigurations(ConfiguredTarget configuredTargets) {
        logger.warn("Detected use of deprecated method 'setTargetConfigurations()'. Existing " + 
                    "method calls should be moved to the new method 'setConfiguredTargets()'.");
        setConfiguredTargets(Arrays.asList(configuredTargets));
    }

    /**
     * This method is deprecated and should not be used. Configured Targets are now represented as a
     * {@link List} of {@link ConfiguredTarget}'s instead of a single {@link ConfiguredTarget} attribute.
     * @Deprecated this method is replaced by {@link #getConfiguredTargets()}
     */
    @Deprecated
    public ConfiguredTarget getTargetConfigurations() {
        logger.warn("Detected use of deprecated method 'getTargetConfigurations()'. Existing " + 
                    "method calls should be moved to the new method 'getConfiguredTargets()'.");
        return getConfiguredTargets().isEmpty() ? null : getConfiguredTargets().get(0);
    }

    /**
     * Method that sets any {@link ConfiguredTarget}'s for the rule. It's up to
     * the invocation engine if they will actually be used, but they should be
     * available to the rule.
     * 
     * @param configuredTargets
     */
    public void setConfiguredTargets(List<ConfiguredTarget> configuredTargets) {
        this.configuredTargets = configuredTargets;
    }

    /**
     * Returns the all the {@link ConfiguredTarget}'s of this rule.
     * 
     * @return list of configured targets
     */
    public List<ConfiguredTarget> getConfiguredTargets() {
        return this.configuredTargets;
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
