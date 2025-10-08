package com.boozallen.aiops.mda.generator;

/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
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
