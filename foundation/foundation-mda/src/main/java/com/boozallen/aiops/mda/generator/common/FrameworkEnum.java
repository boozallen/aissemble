package com.boozallen.aiops.mda.generator.common;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.fasterxml.jackson.annotation.JsonProperty;

public enum FrameworkEnum {

    @JsonProperty("pyspark")
    PYSPARK("pyspark");

    private String frameworkName;

    FrameworkEnum(String frameworkName) {
        this.frameworkName = frameworkName;
    }

    public boolean equalsIgnoreCase(String compareType){
        return this.frameworkName.equalsIgnoreCase(compareType);
    }
}
