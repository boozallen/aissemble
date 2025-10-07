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

import org.technologybrewery.fermenter.mda.generator.AbstractModelAgnosticGenerator;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

import com.boozallen.aiops.mda.ManualActionNotificationService;

/**
 * Generates configuration code with no model interaction. This is often useful for
 * configuration files that must exist in some form or similar constructs.
 */
public class ModelAgnosticResourcesGenerator extends AbstractModelAgnosticGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                          | Template                                          | Generated File                                  |
     * |---------------------------------|---------------------------------------------------|-------------------------------------------------|
     * | cdiBeansXml                     | beans.xml.vm                                      | META-INF/beans.xml                              |
     * | cucumberPipelineFeature         | cucumber.pipeline.feature.vm                      | specifications/pipeline.feature                 |
     * | cucumberProperties              | cucumber.properties.vm                            | cucumber.properties                             |
     * | sparkDataDeliveryProperties     | general-docker/spark.data.delivery.properties.vm  | krausening/base/spark-data-delivery.properties  |
     * | authConfigResource              | general-mlflow/auth.properties.vm                 | krausening/base/auth.properties                 |
     * | inferenceConfigResource         | general-mlflow/inference.config.properties.vm     | krausening/base/inference.properties            |
     * | trainingPipelineConfigResource  | general-mlflow/training.config.properties.vm      | krausening/base/pipeline.properties             |
     * | testLog4jConfiguration          | log4j2.xml.vm                                     | log4j2.xml                                      |
     * | globalDeploymentConfigFile      | pipeline-models/deployment-config.json.vm         | deployment-config.json                          |
     */


    protected ManualActionNotificationService manualActionNotificationService = new ManualActionNotificationService();

	@Override
	protected String getOutputSubFolder() {
		return "resources/";
	}

	@Override
	public void generate(GenerationContext context) {
		super.generate(context);

        // Add the config store manual action
        manualActionNotificationService.addDeployPomMessage(context, "configuration-store-deploy-v2", "configuration-store");
	}

}
