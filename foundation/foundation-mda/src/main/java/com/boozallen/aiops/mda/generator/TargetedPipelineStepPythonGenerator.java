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
import com.boozallen.aiops.mda.generator.util.MavenUtil;
import com.boozallen.aiops.mda.generator.util.PipelineUtils;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import com.boozallen.aiops.mda.metamodel.element.Step;
import com.boozallen.aiops.mda.metamodel.element.python.PythonPipeline;
import com.boozallen.aiops.mda.metamodel.element.python.PythonStep;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

/**
 * Generates Python source modules for each step of the pipeline specified by
 * the {@code targetedPipeline} property in the {@code fermenter-mda} plugin
 * configuration.
 */
public abstract class TargetedPipelineStepPythonGenerator extends AbstractPythonGenerator {
    //--- ALERT: This generator is probably not used by any targets in targets.json

    @Override
    public void generate(GenerationContext generationContext) {
        String baseOutputFile = generationContext.getOutputFile();
        Pipeline pipeline = PipelineUtils.getTargetedPipeline(generationContext, metadataContext);
        PythonPipeline pythonTargetPipeline = new PythonPipeline(pipeline);
        pythonTargetPipeline.validate();

        for (Step step : pythonTargetPipeline.getSteps()) {
            if (shouldGenerateStep(step, generationContext)) {
                VelocityContext vc = getNewVelocityContext(generationContext);
                String profileName = MavenUtil.getPySparkDataDeliveryProfileName(generationContext);
                PythonStep pythonStep = new PythonStep(step);
                pythonStep.validate();
                pythonStep.setProfileName(profileName);
                pythonStep.setRootArtifactId(generationContext.getRootArtifactId());
                vc.put(VelocityProperty.STEP, pythonStep);
                vc.put(VelocityProperty.PIPELINE, pythonTargetPipeline);

                String fileName = replace("name", baseOutputFile, pythonStep.getLowercaseSnakeCaseName());
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
