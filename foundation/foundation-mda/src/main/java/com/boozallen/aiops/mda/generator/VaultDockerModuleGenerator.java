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
import com.boozallen.aiops.mda.metamodel.AissembleModelInstanceRepository;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

import java.util.Map;

/**
 * Generates the vault docker module.
 */
public class VaultDockerModuleGenerator extends AbstractMavenModuleGenerator {
    /*--~-~-~~
     * Usages:
     * | Target              | Template                                | Generated File               |
     * |---------------------|-----------------------------------------|------------------------------|
     * | vaultDockerPomFile  | general-docker/vault.docker.pom.xml.vm  | ${moduleArtifactId}/pom.xml  |
     */


    @Override
    public void generate(GenerationContext context) {
        AissembleModelInstanceRepository metamodelRepository = (AissembleModelInstanceRepository) context.getModelInstanceRepository();

        Map<String, Pipeline> pipelineMap = metamodelRepository.getPipelinesByContext(metadataContext);


        if (!pipelineMap.isEmpty()) {
            VelocityContext vc = getNewVelocityContext(context);

            String artifactId = context.getArtifactId().replace("-docker", "-vault-docker");
            vc.put(VelocityProperty.ARTIFACT_ID, artifactId);
            vc.put(VelocityProperty.BASE_PACKAGE, context.getBasePackage());
            vc.put(VelocityProperty.PARENT_ARTIFACT_ID, context.getArtifactId());
            vc.put(VelocityProperty.PARENT_DESCRIPTIVE_NAME, context.getDescriptiveName());

            String basefileName = context.getOutputFile();
            String fileName = replace("moduleArtifactId", basefileName, artifactId);
            context.setOutputFile(fileName);

            generateFile(context, vc);
            final String appName = "vault";
            manualActionNotificationService.addNoticeToAddModuleToParentBuild(context, artifactId, "docker");
            manualActionNotificationService.addDeployPomMessage(context, "vault-deploy", appName);
        }
    }
}
