package com.boozallen.aiops.mda.generator;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import static com.boozallen.aiops.mda.generator.util.PipelineUtils.isSynchronousStep;

import com.boozallen.aiops.mda.metamodel.element.Step;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

public class SynchronousStepPythonGenerator extends TargetedPipelineStepPythonGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                           | Template                                                | Generated File        |
     * |----------------------------------|---------------------------------------------------------|-----------------------|
     * | pySparkSynchronousProcessorBase  | data-delivery-pyspark/synchronous.processor.base.py.vm  | step/${name}_base.py  |
     * | pySparkSynchronousProcessorImpl  | data-delivery-pyspark/synchronous.processor.impl.py.vm  | step/${name}.py       |
     */


    @Override
    protected boolean shouldGenerateStep(Step step, GenerationContext generationContext) {
        return isSynchronousStep(step);
    }
}
