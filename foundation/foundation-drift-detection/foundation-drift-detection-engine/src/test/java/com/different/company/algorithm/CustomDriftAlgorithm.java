package com.different.company.algorithm;

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

/**
 * {@link CustomDriftAlgorithm} represents a custom drift algorithm implemented
 * outside the default package. Used for testing the classloading based on
 * algorithm identifier.
 * 
 * @author Booz Allen Hamilton
 *
 */
public class CustomDriftAlgorithm implements DriftAlgorithm {

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
