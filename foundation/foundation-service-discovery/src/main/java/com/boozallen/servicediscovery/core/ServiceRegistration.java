package com.boozallen.servicediscovery.core;

/*-
 * #%L
 * Service Discovery::Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.Map;

import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;

/**
 * Represents a service to be registered in Service Discovery.
 */
public class ServiceRegistration {

    private String name;
    private String type;
    private String endpoint;
    private Map<String, String> metadata;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    /**
     * Convenience method to map the service registration to a vert.x Service
     * Discovery record.
     *
     * @return the service registration mapped to a vert.x Service Discovery
     * record
     */
    public Record mapToServiceDiscoveryRecord() {
        Record record = new Record();
        record.setName(getName());
        record.setType(getType());
        record.setLocation(new JsonObject().put("endpoint", getEndpoint()));
        record.setMetadata(new JsonObject());
        if (getMetadata() != null) {
            for (String key : getMetadata().keySet()) {
                String value = getMetadata().get(key);
                record.getMetadata().put(key, value);
            }
        }

        return record;
    }

}
