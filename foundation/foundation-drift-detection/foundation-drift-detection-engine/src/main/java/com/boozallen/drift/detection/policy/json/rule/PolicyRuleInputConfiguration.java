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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * {@link PolicyRuleInputConfiguration} class is used to read in any policy
 * rules from JSON that only have an algorithm and configurations specified.
 * Just keeps the JSON cleaner so that not all the options have to be specified.
 * This class is just used by Jackson while it's reading the policy file.
 * 
 * @author Booz Allen Hamilton
 *
 */
@JsonIgnoreProperties({ "target" })
public class PolicyRuleInputConfiguration extends PolicyRuleInput {

    public PolicyRuleInputConfiguration(String algorithm, Map<String, Object> configuration) {
        super(algorithm, configuration, null);
    }

}
