package com.boozallen.drift.detection.policy.json.rule;

/*-
 * #%L
 * Drift Detection::Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
