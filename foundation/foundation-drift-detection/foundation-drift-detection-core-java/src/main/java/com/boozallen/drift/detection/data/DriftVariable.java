package com.boozallen.drift.detection.data;

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
