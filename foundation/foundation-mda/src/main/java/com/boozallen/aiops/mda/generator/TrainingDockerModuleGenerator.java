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
 * Generates the training docker module if any machine-learning pipelines with a training step exist.
 */
public class TrainingDockerModuleGenerator extends AbstractMavenModuleGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                 | Template                                   | Generated File               |
     * |------------------------|--------------------------------------------|------------------------------|
     * | trainingDockerPomFile  | general-docker/training.docker.pom.xml.vm  | ${moduleArtifactId}/pom.xml  |
     */


    private static final String APP_NAME = "training";

    @Override
    public void generate(GenerationContext context) {
        AissembleModelInstanceRepository metamodelRepository = (AissembleModelInstanceRepository) context.getModelInstanceRepository();

        Map<String, Pipeline> pipelineMap = metamodelRepository.getPipelinesByContext(metadataContext);
        List<Pipeline> pipelines = new ArrayList<>(pipelineMap.values());

        MachineLearningStrategy mlStrategy = new MachineLearningStrategy(pipelines);
        List<String> trainingModules = mlStrategy.getTrainingModules();

        // only generate if a training step has been added to an ML pipeline
        if (!trainingModules.isEmpty()) {
            List<PipelineStepPair> pipelineStepPairs = mlStrategy.getSteps();
            String basefileName = context.getOutputFile();

            for (PipelineStepPair pipelineStepPair : pipelineStepPairs) {
                VelocityContext vc = getNewVelocityContext(context);
                String rootArtifactId = context.getRootArtifactId();
                String trainingDockerArtifactId = getArtifactId(context, pipelineStepPair);
                String trainingModule = deriveArtifactIdFromCamelCase(pipelineStepPair.getStep().getName());

                vc.put(VelocityProperty.ROOT_ARTIFACT_ID, rootArtifactId);
                vc.put(VelocityProperty.ARTIFACT_ID, trainingDockerArtifactId);
                vc.put(VelocityProperty.BASE_PACKAGE, context.getBasePackage());
                vc.put(VelocityProperty.PARENT_ARTIFACT_ID, context.getArtifactId());
                vc.put(VelocityProperty.PARENT_DESCRIPTIVE_NAME, context.getDescriptiveName());
                vc.put(VelocityProperty.TRAINING_PIPELINE, pipelineStepPair);
                vc.put(VelocityProperty.TRAINING_MODULE_SNAKE_CASE, PipelineUtils.deriveLowerSnakeCaseNameFromHyphenatedString(trainingModule));

                String fileName = replace("moduleArtifactId", basefileName, trainingDockerArtifactId);
                context.setOutputFile(fileName);

                generateFile(context, vc);

                // notifications for training docker module
                manualActionNotificationService.addNoticeToAddModuleToParentBuild(context, trainingDockerArtifactId, "docker");
            }
            manualActionNotificationService.addDeployPomMessage(context, "training-deploy", "model-training-api");
            MlflowDockerModuleGenerator.generateManualMessage(context, mlStrategy);
        }
    }

    protected String getArtifactId(GenerationContext context, PipelineStepPair pipelineStepPair) {
        return context.getArtifactId().replace("-docker",
                "-" + deriveArtifactIdFromCamelCase(pipelineStepPair.getStep().getName()) + "-docker");
    }
}
