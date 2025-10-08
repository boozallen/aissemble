package com.boozallen.drift.detection.policy;

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
