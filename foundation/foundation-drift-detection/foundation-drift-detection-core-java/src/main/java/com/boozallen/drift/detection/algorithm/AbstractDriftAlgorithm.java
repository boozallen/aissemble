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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.boozallen.drift.detection.DriftDetectionResult;

public abstract class AbstractDriftAlgorithm implements DriftAlgorithm {

    protected String target;

    protected Map<String, Object> configurations;

    protected DriftDetectionResult createNewBaseResult() {
        DriftDetectionResult result = new DriftDetectionResult();
        Map<String, Object> metadata = result.getMetadata();

        // Add the target to the result
        if (StringUtils.isNotBlank(target)) {
            metadata.put("target", target);
        }

        // Add the configurations that were used to the result
        if (configurations != null && !configurations.isEmpty()) {
            Set<String> keys = configurations.keySet();
            Iterator<String> iter = keys.iterator();
            while (iter.hasNext()) {
                String key = iter.next();
                Object value = configurations.get(key);
                metadata.put(key, value);
            }
        }
        return result;
    }

    /**
     * Method that sets the target that the algorithm should be calculated on.
     * 
     * @param target
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * Returns the target of this algorithm.
     * 
     * @return target
     */
    public String getTarget() {
        return this.target;
    }

    /**
     * Holds any algorithm configurations. It's up to the algorithm
     * implementations if they will actually be used, but they should be
     * available to the algorithm.
     * 
     * @param configurations
     */
    public void setConfigurations(Map<String, Object> configurations) {
        this.configurations = configurations;
    }

    public Map<String, Object> getConfigurations() {
        return this.configurations;
    }

    protected Object getConfiguration(String key) {
        Object configuration = null;
        if (configurations != null && configurations.containsKey(key)) {
            configuration = configurations.get(key);
        }
        return configuration;
    }

}
