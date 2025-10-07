package com.boozallen.aissemble.core.inference;

/*-
 * #%L
 * aiSSEMBLE Foundation::aiSSEMBLE Core
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

import com.fasterxml.jackson.annotation.JsonProperty;

//TODO: Generate this based on record in MDA after migrating to new generated module.
public class InferenceRequest {
    @JsonProperty(value = "source_ip_address", required = true)
    private String sourceIpAddress;
    @JsonProperty(value = "created", required = true)
    private int created;
    @JsonProperty(value = "kind", required = true)
    private String kind;
    @JsonProperty(value = "category", required = true)
    private String category;
    @JsonProperty(value = "outcome", required = true)
    private String outcome;

    public InferenceRequest () {
        // No op for serialization.
    }

    public InferenceRequest(String sourceIpAddress, int created, String kind, String category, String outcome) {
        this.sourceIpAddress = sourceIpAddress;
        this.created = created;
        this.kind = kind;
        this.category = category;
        this.outcome = outcome;
    }

    public String getSourceIpAddress() {
        return sourceIpAddress;
    }

    public void setSourceIpAddress(String sourceIpAddress) {
        this.sourceIpAddress = sourceIpAddress;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }
}
