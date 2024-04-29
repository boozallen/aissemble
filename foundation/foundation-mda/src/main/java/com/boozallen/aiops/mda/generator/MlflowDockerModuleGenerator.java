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
import com.boozallen.aiops.mda.generator.common.MachineLearningStrategy;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class MlflowDockerModuleGenerator {

    protected static ManualActionNotificationService manualActionNotificationService = new ManualActionNotificationService();
    
    public static void generateManualMessage(GenerationContext context, MachineLearningStrategy mlStrategy) {
        manualActionNotificationService.addDeployPomMessage(context,"mlflow-deploy-v2", "mlflow-ui");
        manualActionNotificationService.addDeployPomMessage(context, "s3local-deploy-v2", "s3-local");
        manualActionNotificationService.addDeployPomMessage(context, "aissemble-shared-infrastructure-deploy", "shared-infrastructure");
        manualActionNotificationService.addNoticeToUpdateS3LocalConfig(context, "mlflow-models", Arrays.asList("mlflow-storage"));
        if (mlStrategy.isPostgresNeeded()) {
            manualActionNotificationService.addDeployPomMessage(context, "postgres-deploy", "postgres");
        }
    }
}
