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
import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.metamodel.AIOpsModelInstanceRepostory;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceRepositoryManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Generates the versioning docker module if any machine-learning pipelines have
 * enabled versioning.
 */
public class VersioningDockerModuleGenerator extends AbstractMavenModuleGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                   | Template                                     | Generated File               |
     * |--------------------------|----------------------------------------------|------------------------------|
     * | versioningDockerPomFile  | general-docker/versioning.docker.pom.xml.vm  | ${moduleArtifactId}/pom.xml  |
     */


    @Override
    public void generate(GenerationContext context) {
        AIOpsModelInstanceRepostory metamodelRepository = ModelInstanceRepositoryManager
                .getMetamodelRepository(AIOpsModelInstanceRepostory.class);

        Map<String, Pipeline> pipelineMap = metamodelRepository.getPipelinesByContext(metadataContext);
        List<Pipeline> pipelines = new ArrayList<>(pipelineMap.values());

        MachineLearningStrategy mlStrategy = new MachineLearningStrategy(pipelines);

        // only generate if versioning is enabled for at least one machine-learning pipeline
        if (mlStrategy.isVersioningSupportNeeded()) {
            VelocityContext vc = getNewVelocityContext(context);

            String artifactId = context.getArtifactId().replace("-docker", "-versioning-docker");
            vc.put(VelocityProperty.ARTIFACT_ID, artifactId);
            vc.put(VelocityProperty.BASE_PACKAGE, context.getBasePackage());
            vc.put(VelocityProperty.PARENT_ARTIFACT_ID, context.getArtifactId());
            vc.put(VelocityProperty.PARENT_DESCRIPTIVE_NAME, context.getDescriptiveName());

            String basefileName = context.getOutputFile();
            String fileName = replace("moduleArtifactId", basefileName, artifactId);
            context.setOutputFile(fileName);

            generateFile(context, vc);

            final String appName = "versioning";

            manualActionNotificationService.addNoticeToAddModuleToParentBuild(context, artifactId, "docker");
            DockerBuildParams params = new DockerBuildParams.ParamBuilder()
                    .setContext(context)
                    .setAppName(appName)
                    .setDockerApplicationArtifactId(artifactId)
                    .setDockerArtifactId(context.getArtifactId()).build();
            manualActionNotificationService.addDockerBuildTiltFileMessage(params);
            manualActionNotificationService.addDeployPomMessage(context,"versioning-deploy", appName);
            manualActionNotificationService.addDeployPomMessage(context, "aissemble-shared-infrastructure-deploy", "shared-infrastructure");
        }
    }

}
