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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * 
 * {@link DriftData} provides an interface for referencing the data that will be
 * used for drift detection. This data could be in the form of doubles, int,
 * rows of a spark dataset, etc -- the point of the interface is to allow the
 * user room to customize it to use the data that they need while still having a
 * common API.
 * 
 * @author Booz Allen Hamilton
 *
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME, 
        include = JsonTypeInfo.As.PROPERTY, 
        property = "type")
      @JsonSubTypes({ 
        @Type(value = DriftVariable.class, name = "single"), 
        @Type(value = DriftVariables.class, name = "multiple") 
      })
public interface DriftData {

    public String getName();

}
