package com.boozallen.aiops.mda.generator;

/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.generator.common.PipelineEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

public class MachineLearningResourcesGenerator extends AbstractLineageResourcesGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                   | Template                                     | Generated File                           |
     * |--------------------------|----------------------------------------------|------------------------------------------|
     * | mlflowLineageProperties  | general-docker/mlflow.lineage.properties.vm  | krausening/base/data-lineage.properties  |
     */

    private static final Logger logger = LoggerFactory.getLogger(MachineLearningResourcesGenerator.class);

    @Override
    public void generate(GenerationContext generationContext) {
        setPipelineType(PipelineEnum.MACHINE_LEARNING);
        super.generate(generationContext);
    }
}
