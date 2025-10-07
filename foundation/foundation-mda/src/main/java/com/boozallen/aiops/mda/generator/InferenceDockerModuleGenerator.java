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

import com.boozallen.aiops.mda.generator.common.MachineLearningStrategy;
import com.boozallen.aiops.mda.generator.common.PipelineStepPair;
import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.metamodel.AissembleModelInstanceRepository;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;
import com.boozallen.aiops.mda.generator.util.PipelineUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Generates the inference docker module if any machine-learning pipelines with an inference step exist.
 */
public class InferenceDockerModuleGenerator extends AbstractMavenModuleGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                  | Template                                    | Generated File               |
     * |-------------------------|---------------------------------------------|------------------------------|
     * | inferenceDockerPomFile  | general-docker/inference.docker.pom.xml.vm  | ${moduleArtifactId}/pom.xml  |
     */


    private static final String APP_NAME = "inference";

    @Override
    public void generate(GenerationContext context) {
        AissembleModelInstanceRepository metamodelRepository = (AissembleModelInstanceRepository) context.getModelInstanceRepository();

        Map<String, Pipeline> pipelineMap = metamodelRepository.getPipelinesByContext(metadataContext);
        List<Pipeline> pipelines = new ArrayList<>(pipelineMap.values());

        MachineLearningStrategy mlStrategy = new MachineLearningStrategy(pipelines);
        List<String> inferenceModules = mlStrategy.getInferenceModules();

        // only generate if an inference step has been added to an ML pipeline
        if (!inferenceModules.isEmpty()) {
            List<PipelineStepPair> pipelineStepPairs = mlStrategy.getInferenceSteps();
            String basefileName = context.getOutputFile();

            for (PipelineStepPair pipelineStepPair : pipelineStepPairs) {
                VelocityContext vc = getNewVelocityContext(context);
                String rootArtifactId = context.getRootArtifactId();
                String inferenceDockerArtifactId = getArtifactId(context, pipelineStepPair);
                String inferenceModule = deriveArtifactIdFromCamelCase(pipelineStepPair.getStep().getName());

                vc.put(VelocityProperty.ROOT_ARTIFACT_ID, rootArtifactId);
                vc.put(VelocityProperty.ARTIFACT_ID, inferenceDockerArtifactId);
                vc.put(VelocityProperty.BASE_PACKAGE, context.getBasePackage());
                vc.put(VelocityProperty.PARENT_ARTIFACT_ID, context.getArtifactId());
                vc.put(VelocityProperty.PARENT_DESCRIPTIVE_NAME, context.getDescriptiveName());
                vc.put(VelocityProperty.INFERENCE_PIPELINE, pipelineStepPair);
                vc.put(VelocityProperty.INFERENCE_MODULE_SNAKE_CASE, PipelineUtils.deriveLowerSnakeCaseNameFromHyphenatedString(inferenceModule));

                String fileName = replace("moduleArtifactId", basefileName, inferenceDockerArtifactId);
                context.setOutputFile(fileName);

                generateFile(context, vc);

                // notifications for inference docker module
                manualActionNotificationService.addNoticeToAddModuleToParentBuild(context, inferenceDockerArtifactId, "docker");
                String deployArtifactId = context.getArtifactId().replace("-docker", "-deploy");
                manualActionNotificationService.addHelmfileReleaseMessage(context, inferenceModule, deployArtifactId, rootArtifactId);
                manualActionNotificationService.addDeployPomMessage(context, APP_NAME + "-deploy", inferenceModule);
            }
            MlflowDockerModuleGenerator.generateManualMessage(context, mlStrategy);
        }
    }

    protected String getArtifactId(GenerationContext context, PipelineStepPair pipelineStepPair) {
        return context.getArtifactId().replace("-docker",
                "-" + deriveArtifactIdFromCamelCase(pipelineStepPair.getStep().getName()) + "-docker");
    }
}
