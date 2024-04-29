package com.boozallen.drift.detection.algorithm;

/*-
 * #%L
 * Drift Detection::Domain
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.drift.detection.DriftDetectionResult;
import com.boozallen.drift.detection.data.DriftData;

public interface DriftAlgorithm {

    /**
     * Calculate drift based on a default set of data.
     * 
     * @return drift detection result
     */
    public DriftDetectionResult calculateDrift();

    /**
     * Calculate drift based on passed in input.
     * 
     * @param input
     *            the set of data to calculate drift on
     * @return drift detection result
     */
    public DriftDetectionResult calculateDrift(DriftData input);

    /**
     * Calculate drift based on a set of input data and a set of control data
     * 
     * @param input
     *            the set of data to calculate drift on
     * @param control
     *            the set of control data to use
     * @return drift detection result
     */
    public DriftDetectionResult calculateDrift(DriftData input, DriftData control);
    
}
