package com.boozallen.aiops.mda.util;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import com.boozallen.aiops.mda.metamodel.element.PipelineElement;
import com.boozallen.aiops.mda.metamodel.element.PipelineTypeElement;
import com.boozallen.aiops.mda.metamodel.element.StepElement;

public class TestMetamodelUtil {

    private TestMetamodelUtil() {
    }

    /**
     * Creates a random pipeline step.
     * 
     * @return pipeline step element
     */
    public static StepElement getRandomStep() {
        StepElement step = new StepElement();
        step.setName(RandomStringUtils.randomAlphabetic(10));
        step.setType(RandomStringUtils.randomAlphabetic(5));
        return step;
    }

    /**
     * Creates a pipeline with the given pipeline information.
     * 
     * @param name
     * @param packageName
     * @param typeName
     * @param implementation
     * @return pipeline element
     */
    public static PipelineElement createPipelineWithType(String name, String packageName, String typeName,
            String implementation) {
        PipelineElement newPipeline = new PipelineElement();

        if (StringUtils.isNotBlank(name)) {
            newPipeline.setName(name);
        }

        if (StringUtils.isNotBlank(packageName)) {
            newPipeline.setPackage(packageName);
        }

        PipelineTypeElement type = new PipelineTypeElement();
        if (StringUtils.isNotBlank(typeName)) {
            type.setName(typeName);
        }

        if (StringUtils.isNotBlank(implementation)) {
            type.setImplementation(implementation);
        }

        newPipeline.setType(type);
        return newPipeline;
    }

}
