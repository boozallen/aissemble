package com.boozallen.aissemble.core.policy.configuration.policy;

/*-
 * #%L
 * Policy-Based Configuration::Policy Manager
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
 * {@link ConfiguredTarget} contains the target information with any
 * configurations needed by the rule.
 * 
 * @author Booz Allen Hamilton
 *
 */
public class ConfiguredTarget extends Target {

    @JsonProperty("target_configurations")
    private Map<String, Object> targetConfigurations;

    public ConfiguredTarget(Target target, Map<String, Object> targetConfigurations) {
        if (target != null) {
            setRetrieveUrl(target.getRetrieveUrl());
            setType(target.getType());
        }
        this.targetConfigurations = targetConfigurations;
    }

    public Map<String, Object> getTargetConfigurations() {
        return targetConfigurations;
    }

    public void setTargetConfigurations(Map<String, Object> targetConfigurations) {
        this.targetConfigurations = targetConfigurations;
    }

}
