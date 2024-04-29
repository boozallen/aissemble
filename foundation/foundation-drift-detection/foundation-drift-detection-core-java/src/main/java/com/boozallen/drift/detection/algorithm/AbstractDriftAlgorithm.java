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
