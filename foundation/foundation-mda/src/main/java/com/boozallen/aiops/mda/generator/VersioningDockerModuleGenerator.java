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

import com.boozallen.aiops.mda.generator.common.MachineLearningStrategy;
import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.metamodel.AissembleModelInstanceRepository;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

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
        AissembleModelInstanceRepository metamodelRepository = (AissembleModelInstanceRepository) context.getModelInstanceRepository();

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
            manualActionNotificationService.addDeployPomMessage(context,"versioning-deploy-v2", appName);
            manualActionNotificationService.addDeployPomMessage(context, "aissemble-shared-infrastructure-deploy", "shared-infrastructure");
        }
    }

}
