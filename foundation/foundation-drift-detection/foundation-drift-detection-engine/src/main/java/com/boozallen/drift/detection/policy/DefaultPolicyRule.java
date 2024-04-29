package com.boozallen.drift.detection.policy;

/*-
 * #%L
 * Drift Detection::Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.drift.detection.algorithm.DriftAlgorithm;

public class DefaultPolicyRule implements PolicyRule {

    private DriftAlgorithm algorithm;

    public DefaultPolicyRule() {
        super();
    }

    public DefaultPolicyRule(DriftAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public DriftAlgorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(DriftAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

}
