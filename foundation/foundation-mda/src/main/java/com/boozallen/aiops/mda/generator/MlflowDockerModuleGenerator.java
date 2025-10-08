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

import com.boozallen.aiops.mda.ManualActionNotificationService;
import com.boozallen.aiops.mda.generator.common.MachineLearningStrategy;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

import java.util.Arrays;

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
