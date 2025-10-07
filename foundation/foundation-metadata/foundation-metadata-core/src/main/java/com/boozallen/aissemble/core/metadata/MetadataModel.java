package com.boozallen.aissemble.core.metadata;

/*-
 * #%L
 * aiSSEMBLE Core::Metadata::aiSSEMBLE Metadata
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
import java.util.Map;

/**
 * Class that represents a common metadata model.
 */
public class MetadataModel {
    private String resource;
    private String subject;
    private String action;
    private Instant timestamp;
    private Map<String, String> additionalValues;

    /**
     * Default no-args constructor needed for serialization/deserialization.
     */
    public MetadataModel() {
        this("", "", "");
    }

    /**
     * Common metadata model
     * @param resource the identifier of the data
     * @param subject the thing acting on the data
     * @param action the action being taken
     */
    public MetadataModel(String resource, String subject, String action) {
        this(resource, subject, action, Instant.now(), new HashMap<>());
    }

    /**
     * Common metadata model
     * @param resource the identifier of the data
     * @param subject the thing acting on the data
     * @param action the action being taken
     * @param timestamp the time representing when the action occurred
     */
    public MetadataModel(String resource, String subject, String action, Instant timestamp) {
        this(resource, subject, action, timestamp, new HashMap<>());
    }

    /**
     *
     * @param resource the identifier of the data
     * @param subject the thing acting on the data
     * @param action the action being taken
     * @param timestamp the time representing when the action occurred
     * @param additionalValues additional values to be included in key-value pairs
     */
    public MetadataModel(String resource, String subject, String action, Instant timestamp, Map<String, String> additionalValues) {
        this.resource = resource;
        this.subject = subject;
        this.action = action;
        this.timestamp = timestamp;
        this.additionalValues = additionalValues;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, String> getAdditionalValues() {
        return additionalValues;
    }

    public void setAdditionalValues(Map<String, String> additionalValues) {
        this.additionalValues = additionalValues;
    }
}
