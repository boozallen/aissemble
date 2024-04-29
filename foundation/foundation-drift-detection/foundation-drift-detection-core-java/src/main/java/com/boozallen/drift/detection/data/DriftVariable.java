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
import com.fasterxml.jackson.annotation.JsonSetter;

public class DriftVariable<T> extends AbstractDriftData {

    @JsonProperty
    private T value;

    public DriftVariable() {
        super();
    }

    public DriftVariable(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @JsonSetter
    public void setValue(T value) {
        this.value = value;
        if (value instanceof Double) {
            Double jsonValue = (Double)value;
            if (Double.isNaN(jsonValue)) {
                this.value = null;
            }
        }
    }
}
