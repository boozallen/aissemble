package com.boozallen.drift.detection.policy.json.rule;

/*-
 * #%L
 * Drift Detection::Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * {@link PolicyRuleInput} class represents policy rule data that will be read
 * in from a JSON file.
 * 
 * @author Booz Allen Hamilton
 *
 */
public class PolicyRuleInput {

    /**
     * The target set of data for this rule.
     */
    @JsonProperty
    private String target;

    /**
     * The algorithm that should be used with this rule must be specified for
     * the policy rule to be used.
     */
    @JsonProperty
    private String algorithm;

    /**
     * The configuration used for the algorithm.
     */
    @JsonProperty
    private Map<String, Object> configuration;

    public PolicyRuleInput() {
        super();
    }

    public PolicyRuleInput(String algorithm) {
        this.algorithm = algorithm;
    }

    public PolicyRuleInput(String algorithm, Map<String, Object> configuration, String target) {
        this.algorithm = algorithm;
        this.configuration = configuration;
        this.target = target;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public Map<String, Object> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Map<String, Object> ruleConfiguration) {
        this.configuration = ruleConfiguration;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

}
