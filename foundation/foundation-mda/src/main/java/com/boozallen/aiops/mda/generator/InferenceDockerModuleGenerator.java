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

import com.boozallen.aiops.mda.generator.common.MachineLearningStrategy;
import com.boozallen.aiops.mda.generator.common.PipelineStepPair;
import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.metamodel.AIOpsModelInstanceRepostory;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceRepositoryManager;
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
        AIOpsModelInstanceRepostory metamodelRepository = ModelInstanceRepositoryManager
                .getMetamodelRepository(AIOpsModelInstanceRepostory.class);

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
                String pipelineModule = deriveArtifactIdFromCamelCase(pipelineStepPair.getPipeline().getName());
                manualActionNotificationService.addDockerBuildWithLiveUpdateTiltFileMessage(context, 
                        context.getArtifactId(), inferenceDockerArtifactId, pipelineModule, inferenceModule, false);
                String deployArtifactId = context.getArtifactId().replace("-docker", "-deploy");
                manualActionNotificationService.addHelmTiltFileMessage(context, inferenceModule, deployArtifactId);
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
