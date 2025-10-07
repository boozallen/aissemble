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

import com.boozallen.aiops.mda.ManualActionNotificationService;
import com.boozallen.aiops.mda.generator.common.DataFlowStrategy;
import com.boozallen.aiops.mda.generator.common.MachineLearningStrategy;
import com.boozallen.aiops.mda.metamodel.AissembleModelInstanceRepository;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SharedInfrastructureGenerator extends KubernetesGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                           | Template                                                               | Generated File                                               |
     * |----------------------------------|------------------------------------------------------------------------|--------------------------------------------------------------|
     * | bomPersistentVolumeClaimFile     | deployment/persistentvolumeclaim/bom.persistentvolumeclaim.yaml.vm     | apps/${appName}/templates/bom-persistentvolumeclaim.yaml     |
     * | mlrunsPersistentVolumeClaimFile  | deployment/persistentvolumeclaim/mlruns.persistentvolumeclaim.yaml.vm  | apps/${appName}/templates/mlruns-persistentvolumeclaim.yaml  |
     * | modelPersistentVolumeClaimFile   | deployment/persistentvolumeclaim/model.persistentvolumeclaim.yaml.vm   | apps/${appName}/templates/model-persistentvolumeclaim.yaml   |
     */

    protected ManualActionNotificationService manualActionNotificationService = new ManualActionNotificationService();

    public void generate(GenerationContext context) {
        AissembleModelInstanceRepository metamodelRepository = (AissembleModelInstanceRepository) context.getModelInstanceRepository();

        Map<String, Pipeline> pipelineMap = metamodelRepository.getPipelinesByContext(metadataContext);
        // Get the ML pipelines
        List<Pipeline> pipelines = new ArrayList<>(pipelineMap.values());
        DataFlowStrategy dataFlowStrategy = new DataFlowStrategy(pipelines);
        MachineLearningStrategy mlStrategy = new MachineLearningStrategy(pipelines);

        if (mlStrategy.isVersioningSupportNeeded() || mlStrategy.isMlflowNeeded() || mlStrategy.isAirflowNeeded() || dataFlowStrategy.isAirflowNeeded()) {
            super.generate(context);
            final String projectName = context.getRootArtifactId();
            manualActionNotificationService.addHelmfileReleaseMessage(context, "shared-infrastructure",
                    context.getArtifactId(), projectName);
        }
    }
}
