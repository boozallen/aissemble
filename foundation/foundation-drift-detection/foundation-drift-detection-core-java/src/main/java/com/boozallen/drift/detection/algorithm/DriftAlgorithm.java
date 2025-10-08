package com.boozallen.drift.detection.algorithm;

/*-
 * #%L
 * Drift Detection::Domain
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
