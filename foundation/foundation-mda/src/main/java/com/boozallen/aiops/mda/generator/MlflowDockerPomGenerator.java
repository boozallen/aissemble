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
import com.boozallen.aiops.mda.generator.common.DataFlowStrategy;
import com.boozallen.aiops.mda.generator.common.MachineLearningStrategy;
import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.metamodel.AIOpsModelInstanceRepostory;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceRepositoryManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Iterates through each pipeline in the metamodel and generates the
 * Airflow docker module if there is an ML pipeline.
 */
public class MlflowDockerPomGenerator extends AbstractMavenModuleGenerator {
    /*--~-~-~~
     * Usages:
     * | Target               | Template                                 | Generated File               |
     * |----------------------|------------------------------------------|------------------------------|
     * | mlflowDockerPomFile  | general-docker/mlflow.docker.pom.xml.vm  | ${moduleArtifactId}/pom.xml  |
     */


    private static final String MODULE_ARTIFACT_ID = "moduleArtifactId";

    /**
     * {@inheritDoc}
     */
    @Override
    public void generate(GenerationContext context) {
        AIOpsModelInstanceRepostory metamodelRepository = ModelInstanceRepositoryManager
                .getMetamodelRepository(AIOpsModelInstanceRepostory.class);

        Map<String, Pipeline> pipelineMap = metamodelRepository.getPipelinesByContext(metadataContext);

        String fileName;
        String basefileName = context.getOutputFile();
        basefileName = replaceBasePackage(basefileName, context.getBasePackageAsPath());

        // Get the ML pipelines
        List<Pipeline> pipelines = new ArrayList<>(pipelineMap.values());
        DataFlowStrategy dataFlowStrategy = new DataFlowStrategy(pipelines);
        MachineLearningStrategy mlStrategy = new MachineLearningStrategy(pipelines);

        // Only generate if there is an ML pipeline and mlflow is needed
        if (mlStrategy.isMlflowNeeded()) {
            VelocityContext vc = new VelocityContext();
            String rootArtifactId = context.getRootArtifactId();
            String mlflowDockerArtifactId = context.getArtifactId().replace("-docker", "-mlflow-docker");

            vc.put(VelocityProperty.ROOT_ARTIFACT_ID, rootArtifactId);
            vc.put(VelocityProperty.ARTIFACT_ID, mlflowDockerArtifactId);
            vc.put(VelocityProperty.GROUP_ID, context.getGroupId());
            vc.put(VelocityProperty.BASE_PACKAGE, context.getBasePackage());
            vc.put(VelocityProperty.PARENT_ARTIFACT_ID, context.getArtifactId());
            vc.put(VelocityProperty.VERSION, context.getVersion());
            vc.put(VelocityProperty.PARENT_DESCRIPTIVE_NAME, context.getDescriptiveName());
            vc.put(VelocityProperty.TRAINING_PIPELINES, mlStrategy.getSteps());
            vc.put(VelocityProperty.ENABLE_POSTGRES_SUPPORT, mlStrategy.isPostgresNeeded());
            vc.put(VelocityProperty.USE_S3_LOCAL, true);

            fileName = replace(MODULE_ARTIFACT_ID, basefileName, mlflowDockerArtifactId);
            context.setOutputFile(fileName);

            generateFile(context, vc);

            manualActionNotificationService.addNoticeToAddModuleToParentBuild(context, mlflowDockerArtifactId, "docker");
            DockerBuildParams params = new DockerBuildParams.ParamBuilder()
                    .setContext(context)
                    .setAppName("mlflow-ui")
                    .setDockerApplicationArtifactId(mlflowDockerArtifactId)
                    .setDockerArtifactId(context.getArtifactId()).build();
            manualActionNotificationService.addDockerBuildTiltFileMessage(params);
            manualActionNotificationService.addDeployPomMessage(context, "s3local-deploy-v2", "s3-local");
            manualActionNotificationService.addDeployPomMessage(context, "aissemble-shared-infrastructure-deploy", "shared-infrastructure");
            manualActionNotificationService.addNoticeToUpdateS3LocalConfig(context, "mlflow-models", Arrays.asList("mlflow-storage"));
            if (mlStrategy.isPostgresNeeded()) {
                manualActionNotificationService.addDeployPomMessage(context, "postgres-deploy", "postgres");
            }
        }
    }


}
