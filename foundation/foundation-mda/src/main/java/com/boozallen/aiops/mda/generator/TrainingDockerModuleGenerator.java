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

import com.boozallen.aiops.mda.DockerBuildParams;
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
        AIOpsModelInstanceRepostory metamodelRepository = ModelInstanceRepositoryManager
                .getMetamodelRepository(AIOpsModelInstanceRepostory.class);

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
                DockerBuildParams params = new DockerBuildParams.ParamBuilder()
                        .setContext(context)
                        .setAppName(trainingDockerArtifactId)
                        .setDockerApplicationArtifactId(trainingDockerArtifactId)
                        .setDockerArtifactId(context.getArtifactId())
                        .setDeployedAppName(trainingDockerArtifactId)
                        .setIncludeHelmBuild(false)
                        .setIncludeLatestTag(true).build();
                manualActionNotificationService.addDockerBuildTiltFileMessage(params);
                String pipelineModule = deriveArtifactIdFromCamelCase(pipelineStepPair.getPipeline().getName());
                manualActionNotificationService.addLocalResourceTiltFileMessage(context, context.getArtifactId(), trainingDockerArtifactId, trainingModule, pipelineModule + "/" + trainingModule, true);
                manualActionNotificationService.addDeployPomMessage(context, APP_NAME + "-job", trainingModule + "-image");
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
