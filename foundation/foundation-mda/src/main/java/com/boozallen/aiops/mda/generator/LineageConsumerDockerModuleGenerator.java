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
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

/**
 * Generates the policy decision point docker module.
 */
public class LineageConsumerDockerModuleGenerator extends AbstractMavenModuleGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                                  | Template                                                  | Generated File               |
     * |-----------------------------------------|-----------------------------------------------------------|------------------------------|
     * | dataLineageCustomConsumerDockerPomFile  | general-docker/custom.lineage.consumer.docker.pom.xml.vm  | ${moduleArtifactId}/pom.xml  |
     */

    @Override
    public void generate(GenerationContext context) {
        VelocityContext vc = getNewVelocityContext(context);

        String artifactId = context.getArtifactId().replace("-docker", "-custom-lineage-consumer-docker");
        vc.put(VelocityProperty.ARTIFACT_ID, artifactId);
        vc.put(VelocityProperty.BASE_PACKAGE, context.getBasePackage());
        vc.put(VelocityProperty.PARENT_ARTIFACT_ID, context.getArtifactId());
        vc.put(VelocityProperty.PARENT_DESCRIPTIVE_NAME, context.getDescriptiveName());
        vc.put(VelocityProperty.STEP_ARTIFACT_ID, context.getPropertyVariables().get("appName"));

        String basefileName = context.getOutputFile();
        String fileName = replace("moduleArtifactId", basefileName, artifactId);
        context.setOutputFile(fileName);

        generateFile(context, vc);
        final String appName = "lineage-custom-consumer";
        manualActionNotificationService.addNoticeToAddModuleToParentBuild(context, artifactId, "docker");
        manualActionNotificationService.addDeployPomMessage(context, "aissemble-custom-lineage-consumer-deploy-v2", appName);
    }
}
