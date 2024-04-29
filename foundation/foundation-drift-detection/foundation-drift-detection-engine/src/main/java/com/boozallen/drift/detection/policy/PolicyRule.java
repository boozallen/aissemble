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

/**
 * {@link PolicyRule} interface is used to hold the configured
 * {@link DriftAlgorithm} that was read in.
 * 
 * @author Booz Allen Hamilton
 *
 */
public interface PolicyRule {

    public DriftAlgorithm getAlgorithm();

}
