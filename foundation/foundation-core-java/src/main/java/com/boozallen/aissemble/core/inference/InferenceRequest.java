package com.boozallen.aissemble.core.inference;

/*-
 * #%L
 * aiSSEMBLE Foundation::aiSSEMBLE Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
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
