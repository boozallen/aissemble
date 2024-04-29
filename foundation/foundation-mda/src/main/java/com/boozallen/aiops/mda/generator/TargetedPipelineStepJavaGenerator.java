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

import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

import com.boozallen.aiops.mda.generator.util.PipelineUtils;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import com.boozallen.aiops.mda.metamodel.element.Step;
import com.boozallen.aiops.mda.metamodel.element.java.JavaPipeline;
import com.boozallen.aiops.mda.metamodel.element.java.JavaStep;

/**
 * Iterates through each pipeline in the metamodel and enables the generation of a single file for each pipeline.
 */
public abstract class TargetedPipelineStepJavaGenerator extends AbstractJavaGenerator {
    //--- ALERT: This generator is probably not used by any targets in targets.json

    /**
     * {@inheritDoc}
     */
    @Override
    public void generate(GenerationContext generationContext) {
        super.generate(generationContext);

        String baseOutputFile = generationContext.getOutputFile();

        Pipeline pipeline = PipelineUtils.getTargetedPipeline(generationContext, metadataContext);
        JavaPipeline javaTargetPipeline = new JavaPipeline(pipeline);
        javaTargetPipeline.validate();

        for (Step step : javaTargetPipeline.getSteps()) {
            if (shouldGenerateStep(step, generationContext)) {
                VelocityContext vc = getNewVelocityContext(generationContext);
                vc.put(VelocityProperty.PIPELINE, javaTargetPipeline);
                JavaStep javaStep = new JavaStep(step);
                javaStep.validate();
                vc.put(VelocityProperty.STEP, javaStep);

                String fileName = replace("name", baseOutputFile, step.getName());
                generationContext.setOutputFile(fileName);

                generateFile(generationContext, vc);
            }
        }
    }

    /**
     * Provides an optional opportunity to short circuit generation for steps. This is often useful if extending to
     * target specific steps to specific templates.
     * 
     * @param step
     *            step being considered
     * @param generationContext
     *            context of generation
     * @return whether or not to generate
     */
    protected boolean shouldGenerateStep(Step step, GenerationContext generationContext) {
        return true;
    }

}
