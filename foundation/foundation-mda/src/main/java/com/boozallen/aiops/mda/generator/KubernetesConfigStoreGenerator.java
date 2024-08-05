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

import org.technologybrewery.fermenter.mda.generator.GenerationContext;

public class KubernetesConfigStoreGenerator extends AbstractKubernetesGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                                      | Template                                                                  | Generated File                   |
     * |---------------------------------------------|---------------------------------------------------------------------------|----------------------------------|
     * | aissembleConfigurationStoreArgoCDV2         | deployment/argocd/v2/configuration-store.yaml.vm                          | templates/${appName}.yaml        |
     * | aissembleConfigurationStoreHelmChartFileV2  | deployment/configuration-store/v2/configuration-store.chart.yaml.vm       | apps/${appName}/Chart.yaml       |
     * | aissembleConfigurationStoreValuesDevFileV2  | deployment/configuration-store/v2/configuration-store.values-dev.yaml.vm  | apps/${appName}/values-dev.yaml  |
     * | aissembleConfigurationStoreValuesFileV2     | deployment/configuration-store/v2/configuration-store.values.yaml.vm      | apps/${appName}/values.yaml      |
     */

    @Override
    protected void addTiltNotification(GenerationContext generationContext, String appName, String deployArtifactId) {
        getNotificationService().addHelmTiltFileMessage(generationContext, appName, deployArtifactId, true);
    }

}
