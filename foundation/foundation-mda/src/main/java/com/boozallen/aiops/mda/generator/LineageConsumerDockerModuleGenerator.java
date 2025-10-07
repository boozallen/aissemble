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
