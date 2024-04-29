package com.boozallen.drift.detection.data;

/*-
 * #%L
 * Drift Detection::Domain
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.fasterxml.jackson.annotation.JsonProperty;

public class DriftDataInput {
    
    @JsonProperty
    private DriftData input;
    
    @JsonProperty
    private DriftData control;

    public DriftData getInput() {
        return input;
    }

    public void setInput(DriftData input) {
        this.input = input;
    }

    public DriftData getControl() {
        return control;
    }

    public void setControl(DriftData control) {
        this.control = control;
    }

}
