package com.different.company.algorithm;

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
