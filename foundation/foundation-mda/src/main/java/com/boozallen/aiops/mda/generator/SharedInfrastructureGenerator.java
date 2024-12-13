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
            manualActionNotificationService.addHelmTiltFileMessage(context, "shared-infrastructure", context.getArtifactId());
        }
    }
}
