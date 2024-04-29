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

import static com.boozallen.aiops.mda.generator.util.PipelineUtils.isAsynchronousStep;

import org.technologybrewery.fermenter.mda.generator.GenerationContext;

import com.boozallen.aiops.mda.metamodel.element.Step;

/**
 * Generate synchronous step implementations for the target pipeline.
 */
public class SynchronousStepJavaGenerator extends TargetedPipelineStepJavaGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                        | Template                                                | Generated File                   |
     * |-------------------------------|---------------------------------------------------------|----------------------------------|
     * | javaSynchronousProcessorBase  | data-delivery-spark/synchronous.processor.base.java.vm  | ${basePackage}/${name}Base.java  |
     * | javaSynchronousProcessorImpl  | data-delivery-spark/synchronous.processor.impl.java.vm  | ${basePackage}/${name}.java      |
     */


    @Override
    protected boolean shouldGenerateStep(Step step, GenerationContext generationContext) {
        return !isAsynchronousStep(step);
    } 
    
}
