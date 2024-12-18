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
import com.boozallen.aiops.mda.generator.util.PythonGeneratorUtils;
import com.boozallen.aiops.mda.metamodel.AissembleModelInstanceRepository;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import com.boozallen.aiops.mda.metamodel.element.Step;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.boozallen.aiops.mda.generator.util.PipelineUtils.isGenericStep;

/**
 * Generates a maven module for each step in the pipeline specified by the
 * targetedPipeline property in the fermenter-mda plugin.
 */
public class TargetedPipelineStepModuleGenerator extends AbstractMavenModuleGenerator {
    /*--~-~-~~
     * Usages:
     * | Target               | Template                                 | Generated File                                   |
     * |----------------------|------------------------------------------|--------------------------------------------------|
     * | pipelineStepPomFile  | general-mlflow/pipeline.step.pom.xml.vm  | ${pipelineArtifactId}/${stepArtifactId}/pom.xml  |
     */

    @Override
    public void generate(GenerationContext generationContext) {
        AissembleModelInstanceRepository metamodelRepository = (AissembleModelInstanceRepository) generationContext.getModelInstanceRepository();
        String targetPipelineName = getTargetPipeline(generationContext);

        // checking the missing modules from the ml pipeline directory
        if (targetPipelineName != null) {
            Pipeline targetPipeline = metamodelRepository.getPipelinesByContext(metadataContext).get(targetPipelineName);
            for (Step step : targetPipeline.getSteps()) {
                manualActionNotificationService.addNoticeToAddModuleToParentBuild(generationContext, deriveArtifactIdFromCamelCase(step.getName()), "step");
            }
            return;
        }

        List<Pipeline> mlPipelines = getMlPipelines(metamodelRepository);
        if (mlPipelines != null && mlPipelines.size() > 0) {
            for (Pipeline pipeline: mlPipelines) {
                String pipelineName = pipeline.getName();

                String baseFileName = generationContext.getOutputFile();

                for (Step step : pipeline.getSteps()) {
                    if (isGenericStep(step)) { continue; }

                    VelocityContext vc = super.getNewVelocityContext(generationContext);
                    vc.put(VelocityProperty.PARENT_DESCRIPTIVE_NAME, generationContext.getDescriptiveName());
                    vc.put(VelocityProperty.PARENT_ARTIFACT_ID, generationContext.getArtifactId());

                    vc.put(VelocityProperty.PIPELINE, pipeline);
                    String parentArtifactId = deriveArtifactIdFromCamelCase(pipelineName);
                    vc.put(VelocityProperty.PIPELINE_ARTIFACT_ID, parentArtifactId);

                    String stepName = step.getName();
                    String stepArtifactId = deriveArtifactIdFromCamelCase(stepName);
                    vc.put(VelocityProperty.STEP, step);
                    vc.put(VelocityProperty.STEP_ARTIFACT_ID, stepArtifactId);
                    vc.put(VelocityProperty.STEP_ARTIFACT_ID_SNAKE_CASE, PythonGeneratorUtils.normalizeToPythonCase(stepArtifactId));
                    vc.put(VelocityProperty.DESCRIPTIVE_NAME, deriveDescriptiveNameFromCamelCase(stepName));
                    vc.put(VelocityProperty.MODULE_ARTIFACT_ID_PYTHON_CASE, PythonGeneratorUtils.normalizeToPythonCase(stepArtifactId));

                    String fileName = replace(VelocityProperty.PIPELINE_ARTIFACT_ID, baseFileName, parentArtifactId);
                    fileName = replace(VelocityProperty.STEP_ARTIFACT_ID, fileName, stepArtifactId);
                    generationContext.setOutputFile(fileName);

                    generateFile(generationContext, vc);
                }
            }
        }
    }

    private List<Pipeline> getMlPipelines(AissembleModelInstanceRepository metamodelRepository) {
        Map<String, Pipeline> pipelineMap = metamodelRepository.getPipelinesByContext(metadataContext);
        return pipelineMap.values()
                .stream()
                .filter(pipeline -> "machine-learning".equals(pipeline.getType().getName()))
                .collect(Collectors.toList());
    }

    private String getTargetPipeline(GenerationContext generationContext) {
        Map<String, String> generationPropertyVariables = generationContext.getPropertyVariables();
        return generationPropertyVariables.get("targetPipeline");
    }

}
