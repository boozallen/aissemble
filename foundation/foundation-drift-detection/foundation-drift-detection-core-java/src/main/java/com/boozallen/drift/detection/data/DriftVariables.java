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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DriftVariables<T> extends AbstractDriftData {

    @JsonProperty
    private List<DriftVariable<T>> variables;

    public DriftVariables() {
        super();
    }

    public DriftVariables(List<DriftVariable<T>> variables) {
        this.variables = variables;
    }

    public List<DriftVariable<T>> getVariables() {
        return variables;
    }

    public void setVariables(List<DriftVariable<T>> variables) {
        this.variables = variables;
    }

}
