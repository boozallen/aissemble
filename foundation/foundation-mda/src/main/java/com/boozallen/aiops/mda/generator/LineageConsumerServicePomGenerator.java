package com.boozallen.aiops.mda.generator;

/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

public class LineageConsumerServicePomGenerator extends AbstractMavenModuleGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                            | Template                                                | Generated File               |
     * |-----------------------------------|---------------------------------------------------------|------------------------------|
     * | dataLineageCustomConsumerPomFile  | data-lineage-consumer/data-lineage-consumer.pom.xml.vm  | ${moduleArtifactId}/pom.xml  |
     */

    @Override
    public void generate(GenerationContext context) {
        VelocityContext vc = getNewVelocityContext(context);
        String artifactId = context.getArtifactId().replace("-shared", "-lineage-consumer-service");
        vc.put(VelocityProperty.ARTIFACT_ID, artifactId);
        vc.put(VelocityProperty.BASE_PACKAGE, context.getBasePackage());
        vc.put(VelocityProperty.PARENT_ARTIFACT_ID, context.getArtifactId());
        vc.put(VelocityProperty.PARENT_DESCRIPTIVE_NAME, context.getDescriptiveName());

        String basefileName = context.getOutputFile();
        String fileName = replace("moduleArtifactId", basefileName, artifactId);
        context.setOutputFile(fileName);

        generateFile(context, vc);
        manualActionNotificationService.addDockerPomMessage(context, "aissemble-lineage-consumer-docker-module", artifactId);
        manualActionNotificationService.addNoticeToAddModuleToParentBuild(context, artifactId, "shared");
    }
}
