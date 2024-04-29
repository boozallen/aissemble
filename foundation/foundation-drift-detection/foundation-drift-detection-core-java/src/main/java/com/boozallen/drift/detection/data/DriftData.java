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
