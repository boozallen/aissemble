package com.boozallen.aiops.mda.generator.common;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
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
