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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * {@link PolicyRuleInputAlgorithm} class is used to read in any policy rules
 * from JSON that only have an algorithm specified. Just keeps the JSON cleaner
 * so that not all the options have to be specified. This class is just used by
 * Jackson while it's reading the policy file.
 * 
 * @author Booz Allen Hamilton
 *
 */
@JsonIgnoreProperties({ "target", "configuration" })
public class PolicyRuleInputAlgorithm extends PolicyRuleInput {

    public PolicyRuleInputAlgorithm(String algorithm) {
        super(algorithm, null, null);
    }
}
