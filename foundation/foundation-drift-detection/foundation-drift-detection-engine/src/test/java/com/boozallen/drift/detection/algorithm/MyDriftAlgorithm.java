package com.boozallen.drift.detection.algorithm;

/*-
 * #%L
 * Drift Detection::Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.drift.detection.DriftDetectionResult;
import com.boozallen.drift.detection.algorithm.DriftAlgorithm;
import com.boozallen.drift.detection.data.DriftData;

public class MyDriftAlgorithm implements DriftAlgorithm {

    @Override
    public DriftDetectionResult calculateDrift() {
        return new DriftDetectionResult();
    }

    @Override
    public DriftDetectionResult calculateDrift(DriftData input) {
        return new DriftDetectionResult();
    }

    @Override
    public DriftDetectionResult calculateDrift(DriftData input, DriftData control) {
        return new DriftDetectionResult();
    }

}
