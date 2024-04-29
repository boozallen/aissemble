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
