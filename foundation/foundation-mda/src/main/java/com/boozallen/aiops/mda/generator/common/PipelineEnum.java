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

import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum PipelineEnum {
    DATA_FLOW("data-flow"),
    MACHINE_LEARNING("machine-learning");

    private String pipelineType;

    private PipelineEnum(String pipelineType) {
        this.pipelineType = pipelineType;
    }

    public boolean equalsIgnoreCase(String compareType){
        return this.pipelineType.equalsIgnoreCase(compareType);
    }

    public static boolean isValidType(String implementation) {
        PipelineEnum[] vals = values();
        for (PipelineEnum enumeration : vals) {
            if (enumeration.equalsIgnoreCase(implementation)) {
                return true;
            }
        }
        return false;
    }

    public static String getPossibleValues() {
        return Stream.of(values())
                .map(type -> type.pipelineType)
                .collect(Collectors.joining(", ", "[", "]"));
    }

    public static String getPipelineType(PipelineEnum pipelineEnum) {
        return pipelineEnum.pipelineType;
    }
}
