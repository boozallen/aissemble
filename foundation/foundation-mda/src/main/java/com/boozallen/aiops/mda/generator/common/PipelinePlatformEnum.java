package com.boozallen.aiops.mda.generator.common;

import java.util.Arrays;
import java.util.List;

/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
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

/**
 * Enum to represent a platform for a pipeline.
 */
public enum PipelinePlatformEnum {

    SEDONA("sedona", PipelineImplementationEnum.DATA_DELIVERY_PYSPARK, PipelineImplementationEnum.DATA_DELIVERY_SPARK);

    private String platformName;
    private List<PipelineImplementationEnum> pipelineImplementations;

    private PipelinePlatformEnum(String platformName, PipelineImplementationEnum... pipelineImplementations) {
        this.platformName = platformName;
        this.pipelineImplementations = Arrays.asList(pipelineImplementations);
    }

    /**
     * Returns the name of the platform.
     * 
     * @return platform name
     */
    public String getPlatformName() {
        return platformName;
    }

    /**
     * Returns the pipeline implementations that are applicable to the platform.
     * 
     * @return pipeline implementations
     */
    public List<PipelineImplementationEnum> getPipelineImplementations() {
        return pipelineImplementations;
    }

    /**
     * Checks whether the given platform name & pipeline implementation
     * combination is valid.
     * 
     * @param platformName
     *            the name of the platform
     * @param pipelineImplementation
     *            the pipeline implementation for the platform
     * @return true if the platform name & pipeline implementation combination
     *         is valid
     */
    public static boolean isValidPipelinePlatform(String platformName, String pipelineImplementation) {
        boolean isValid = false;

        for (PipelinePlatformEnum platform : values()) {
            if (platform.getPlatformName().equalsIgnoreCase(platformName)
                    && platform.isValidPipelineImplementation(pipelineImplementation)) {
                isValid = true;
                break;
            }
        }

        return isValid;
    }

    private boolean isValidPipelineImplementation(String implementationToCheck) {
        boolean isValid = false;

        for (PipelineImplementationEnum implementation : getPipelineImplementations()) {
            if (implementation.equalsIgnoreCase(implementationToCheck)) {
                isValid = true;
                break;
            }
        }

        return isValid;
    }

}
