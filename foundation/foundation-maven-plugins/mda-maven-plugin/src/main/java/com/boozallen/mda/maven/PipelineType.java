package com.boozallen.mda.maven;

/*-
 * #%L
 * Foundation::Maven Plugins::MDA Maven Plugin
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

public enum PipelineType {
    DATA_FLOW("data-flow"),
    ML_TRAINING("machine-learning-training");

    private final String typeName;

    PipelineType(String typeName) {
        this.typeName = typeName;
    }

    public static PipelineType fromString(String typeString) {
        for (PipelineType type : PipelineType.values()) {
            if (type.typeName.equalsIgnoreCase(typeString) || type.name().equalsIgnoreCase(typeString)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unrecognized pipeline type: " + typeString);
    }
}
