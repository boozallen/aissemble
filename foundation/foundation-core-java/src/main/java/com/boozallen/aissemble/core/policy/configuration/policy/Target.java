package com.boozallen.aissemble.core.policy.configuration.policy;

/*-
 * #%L
 * Policy-Based Configuration::Policy Manager
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Target {

    @JsonProperty("retrieve_url")
    private String retrieveUrl;

    @JsonProperty
    private String type;

    /**
     * Default constructor.
     */
    public Target() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param retrieveUrl
     *            where the target data can be retrieved
     * @param type
     *            the type of target this is -- rest, data, hive, etc
     */
    public Target(String retrieveUrl, String type) {
        this.retrieveUrl = retrieveUrl;
        this.type = type;
    }

    public String getRetrieveUrl() {
        return retrieveUrl;
    }

    public void setRetrieveUrl(String retrieveUrl) {
        this.retrieveUrl = retrieveUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonIgnore
    public boolean isValidTarget() {
        boolean isValid = true;
        if (StringUtils.isBlank(retrieveUrl) || StringUtils.isBlank(getType())) {
            isValid = false;
        }
        return isValid;
    }

}
