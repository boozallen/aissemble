package com.boozallen.aiops.mda.generator.record;

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
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

/**
 * Generates the data-access-docker module if needed.
 */
public class DataAccessDockerModuleGenerator extends DataAccessModuleGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                   | Template                                      | Generated File               |
     * |--------------------------|-----------------------------------------------|------------------------------|
     * | dataAccessDockerPomFile  | general-docker/data.access.docker.pom.xml.vm  | ${moduleArtifactId}/pom.xml  |
     */


    @Override
    protected String getModuleArtifactId(GenerationContext context, String appName) {
        String parentArtifactId = context.getArtifactId();
        return parentArtifactId.replace("docker", appName + "-docker");
    }

    @Override
    protected void addBuildNotices(GenerationContext context, String moduleArtifactId, String appName) {
        manualActionNotificationService.addNoticeToAddModuleToParentBuild(context, moduleArtifactId, "docker");
        manualActionNotificationService.addDeployPomMessage(context, appName + "-deploy-v2", appName);
    }

}
