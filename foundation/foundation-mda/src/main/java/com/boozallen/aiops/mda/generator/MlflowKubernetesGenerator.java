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

import com.boozallen.aiops.mda.ManualActionNotificationService;
import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MlflowKubernetesGenerator extends AbstractKubernetesGenerator {
    /*--~-~-~~
     * Usages:
     * | Target               | Template                                     | Generated File                   |
     * |----------------------|----------------------------------------------|----------------------------------|
     * | mlflowArgoCD         | deployment/argocd/mlflow-ui.yaml.vm          | templates/mlflow-ui.yaml         |
     * | mlflowValuesDevFile  | deployment/mlflow/mlflow.values-dev.yaml.vm  | apps/${appName}/values-dev.yaml  |
     * | mlflowValuesFile     | deployment/mlflow/mlflow.values.yaml.vm      | apps/${appName}/values.yaml      |
     */

    /**
     * {@inheritDoc}
    */

   	protected ManualActionNotificationService manualActionNotificationService = new ManualActionNotificationService();

    @Override
    public void generate(GenerationContext context) {
        VelocityContext vc = this.configureWithoutGeneration(context);
        generateFile(context, vc);
        vc.put(VelocityProperty.BASE_PACKAGE, context.getBasePackage());
        manualActionNotificationService.addDockerPomMessage(context, "docker-pom-mlflow", "mlflow");

    }

}
