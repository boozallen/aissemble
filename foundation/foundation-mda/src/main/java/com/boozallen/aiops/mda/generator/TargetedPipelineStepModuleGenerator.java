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
import com.boozallen.aiops.mda.generator.util.PipelineUtils;
import com.boozallen.aiops.mda.generator.util.PythonGeneratorUtils;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import com.boozallen.aiops.mda.metamodel.element.Step;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

import static com.boozallen.aiops.mda.generator.util.PipelineUtils.isGenericStep;

/**
 * Generates a maven module for each step in the pipeline specified by the
 * targetedPipeline property in the fermenter-mda plugin.
 */
public class TargetedPipelineStepModuleGenerator extends AbstractMavenModuleGenerator {
    /*--~-~-~~
     * Usages:
     * | Target               | Template                                 | Generated File             |
     * |----------------------|------------------------------------------|----------------------------|
     * | pipelineStepPomFile  | general-mlflow/pipeline.step.pom.xml.vm  | ${stepArtifactId}/pom.xml  |
     */


    @Override
    public void generate(GenerationContext generationContext) {
        Pipeline pipeline = PipelineUtils.getTargetedPipeline(generationContext, metadataContext);
        String pipelineName = pipeline.getName();

        String baseFileName = generationContext.getOutputFile();

        for (Step step : pipeline.getSteps()) {
            if (isGenericStep(step)) { continue; }

            VelocityContext vc = super.getNewVelocityContext(generationContext);
            vc.put(VelocityProperty.PARENT_DESCRIPTIVE_NAME, generationContext.getDescriptiveName());
			vc.put(VelocityProperty.PARENT_ARTIFACT_ID, generationContext.getArtifactId());

            vc.put(VelocityProperty.PIPELINE, pipeline);
            vc.put(VelocityProperty.PIPELINE_ARTIFACT_ID, deriveArtifactIdFromCamelCase(pipelineName));
            
            String stepName = step.getName();
            String stepArtifactId = deriveArtifactIdFromCamelCase(stepName);
            vc.put(VelocityProperty.STEP, step);
            vc.put(VelocityProperty.STEP_ARTIFACT_ID, stepArtifactId);
            vc.put(VelocityProperty.STEP_ARTIFACT_ID_SNAKE_CASE, PythonGeneratorUtils.normalizeToPythonCase(stepArtifactId));
            vc.put(VelocityProperty.DESCRIPTIVE_NAME, deriveDescriptiveNameFromCamelCase(stepName));
            vc.put(VelocityProperty.MODULE_ARTIFACT_ID_PYTHON_CASE, PythonGeneratorUtils.normalizeToPythonCase(stepArtifactId));

            String fileName = replace(VelocityProperty.STEP_ARTIFACT_ID, baseFileName, stepArtifactId);
            generationContext.setOutputFile(fileName);

            generateFile(generationContext, vc);

            manualActionNotificationService.addNoticeToAddModuleToParentBuild(generationContext, stepArtifactId, "step");
        }
    }

}
