package com.boozallen.aiops.mda.generator;

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
        String targetPipelineName = getTargetPipeline(generationContext);

        if (targetPipelineName != null) {
            handleNewPipelineStep(generationContext, targetPipelineName);
        } else {
            handleFirstTimeGeneration(generationContext);
        }
    }

    private String getTargetPipeline(GenerationContext generationContext) {
        Map<String, String> generationPropertyVariables = generationContext.getPropertyVariables();
        return generationPropertyVariables.get("targetPipeline");
    }

    private void handleNewPipelineStep(GenerationContext generationContext, String targetPipelineName) {
        AissembleModelInstanceRepository metamodelRepository = (AissembleModelInstanceRepository) generationContext.getModelInstanceRepository();
        Pipeline targetPipeline = metamodelRepository.getPipelinesByContext(metadataContext).get(targetPipelineName);
        for (Step step : targetPipeline.getSteps()) {
            manualActionNotificationService.addNoticeToAddModuleToParentBuild(generationContext, deriveArtifactIdFromCamelCase(step.getName()), "step");
        }
    }

    private void handleFirstTimeGeneration(GenerationContext generationContext) {
        AissembleModelInstanceRepository metamodelRepository = (AissembleModelInstanceRepository) generationContext.getModelInstanceRepository();
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

}
