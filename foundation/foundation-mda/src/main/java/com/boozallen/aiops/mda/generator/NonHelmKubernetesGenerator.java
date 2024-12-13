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

import org.technologybrewery.fermenter.mda.generator.GenerationContext;

public class NonHelmKubernetesGenerator extends AbstractKubernetesGenerator {
    /*--~-~-~~
     * Usages:
     * | Target            | Template                                                        | Generated File                   |
     * |-------------------|-----------------------------------------------------------------|----------------------------------|
     * | trainingNoOpFile  | deployment/model-training-job/model-training-job.no-op.yaml.vm  | apps/${appName}/${appName}.yaml  |
     */

    @Override
    protected void addTiltNotification(GenerationContext generationContext, String appName, String deployArtifactId) {
        getNotificationService().addYamlTiltFileMessage(generationContext, appName, deployArtifactId, appName + ".yaml");
    }
}
