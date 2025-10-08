package com.boozallen.drift.detection;

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

import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.boozallen.aissemble.alerting.core.Alert.Status;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Contains the result of running drift detection against a dataset. Note: Might
 * fall under the alerting category but currently doesn't extend since most
 * fields don't really apply. Might be refactored once alerting base components
 * are nailed down more.
 * 
 * @author Booz Allen Hamilton
 *
 */
public class DriftDetectionResult {

    public static final String POLICY_IDENTIFIER = "policyIdentifier";

    public static final String POLICY_DESCRIPTION = "policyDescription";

    public static final String FLAGGED_VALUE = "flaggedValue";

    public static final String DATA_NAME = "inputDataName";

    @JsonProperty
    private Boolean hasDrift = null;

    @JsonProperty
    @JsonDeserialize(using = TimestampDeserializer.class)
    private Instant timestamp = null;

    @JsonProperty
    private Map<String, Object> metadata = new HashMap<String, Object>();

    public Boolean hasDrift() {
        return this.hasDrift;
    }

    public void setDriftDiscovered(Boolean hasDrift) {
        this.hasDrift = hasDrift;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public void addPolicyIdentifier(String policyIdentifier) {
        metadata.put(POLICY_IDENTIFIER, policyIdentifier);
    }

    public void addFlaggedValue(Object value) {
        metadata.put(FLAGGED_VALUE, value);
    }

    public void addDataName(String name) {
        metadata.put(DATA_NAME, name);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getStatusMessage());

        // Include all the metadata if there's drift
        if (hasDrift()) {
            Set<String> keys = metadata.keySet();
            Iterator<String> iter = keys.iterator();
            while (iter.hasNext()) {
                String key = iter.next();
                Object value = metadata.get(key);
                builder.append(key + ": " + value + "\n");
            }
        }

        return builder.toString();
    }

    /**
     * Helper method that builds out a summary of the status of this run of
     * drift detection.
     * 
     * @return statusMessage
     */
    private String getStatusMessage() {

        StringBuilder builder = new StringBuilder();
        String dataName = (metadata.containsKey(DATA_NAME)) ? (String) metadata.get(DATA_NAME) : null;
        if (hasDrift()) {
            builder.append("Drift detected");
            if (StringUtils.isNotBlank(dataName)) {
                builder.append(" on " + dataName);
            }
            builder.append(" at " + getTimestamp());
        } else {
            builder.append("No drift detected");
            if (StringUtils.isNotBlank(dataName)) {
                builder.append(" on " + dataName);
            }
            builder.append(". Processing completed at " + getTimestamp());
        }
        builder.append("\n");
        return builder.toString();
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Status getStatus() {
        Status status = Status.SUCCESS;
        if (hasDrift()) {
            status = Status.FAILURE;
        }
        return status;
    }

}
