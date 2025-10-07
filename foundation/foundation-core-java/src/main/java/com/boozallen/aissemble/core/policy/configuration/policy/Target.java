package com.boozallen.aissemble.core.policy.configuration.policy;

/*-
 * #%L
 * Policy-Based Configuration::Policy Manager
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
