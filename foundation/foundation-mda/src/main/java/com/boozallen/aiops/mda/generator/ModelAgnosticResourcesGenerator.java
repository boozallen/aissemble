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
     * | Target                             | Template                                                                                | Generated File                                       |
     * |------------------------------------|-----------------------------------------------------------------------------------------|------------------------------------------------------|
     * | cdiBeansXml                        | beans.xml.vm                                                                            | META-INF/beans.xml                                   |
     * | cucumberPipelineFeature            | cucumber.pipeline.feature.vm                                                            | specifications/pipeline.feature                      |
     * | cucumberProperties                 | cucumber.properties.vm                                                                  | cucumber.properties                                  |
     * | dataAccessApplicationProperties    | data-access/data.access.application.properties.vm                                       | application.properties                               |
     * | baseSparkInfrastructureProperties  | deployment/spark-infrastructure/configurations/base/spark-infrastructure.properties.vm  | configurations/base/spark-infrastructure.properties  |
     * | envSparkInfrastructureProperties   | deployment/spark-infrastructure/configurations/env/spark-infrastructure.properties.vm   | configurations/env/spark-infrastructure.properties   |
     * | mlflowStartScript                  | general-docker/mlflow.start.sh.vm                                                       | start.sh                                             |
     * | sparkDataDeliveryProperties        | general-docker/spark.data.delivery.properties.vm                                        | krausening/base/spark-data-delivery.properties       |
     * | authConfigResource                 | general-mlflow/auth.properties.vm                                                       | krausening/base/auth.properties                      |
     * | inferenceConfigResource            | general-mlflow/inference.config.properties.vm                                           | krausening/base/inference.properties                 |
     * | trainingPipelineConfigResource     | general-mlflow/training.config.properties.vm                                            | krausening/base/pipeline.properties                  |
     * | itChartYaml                        | integration-test/it.chart.yaml.vm                                                       | test-chart/Chart.yaml                                |
     * | itPipelineSpecification            | integration-test/it.pipeline.spec.vm                                                    | specifications/pipeline.feature                      |
     * | itServiceAccountYaml               | integration-test/it.serviceaccount.yaml.vm                                              | test-chart/templates/serviceaccount.yaml             |
     * | itTestYaml                         | integration-test/it.test.yaml.vm                                                        | test-chart/templates/test.yaml                       |
     * | itTiltfile                         | integration-test/it.tiltfile.vm                                                         | Tiltfile                                             |
     * | itValuesCIYaml                     | integration-test/it.values.ci.yaml.vm                                                   | test-chart/values-ci.yaml                            |
     * | itValuesPipelineYaml               | integration-test/it.values.pipeline.yaml.vm                                             | test-chart/values-pipeline.yaml                      |
     * | itValuesYaml                       | integration-test/it.values.yaml.vm                                                      | test-chart/values.yaml                               |
     * | testLog4jConfiguration             | log4j2.xml.vm                                                                           | log4j2.xml                                           |
     * | globalDeploymentConfigFile         | pipeline-models/deployment-config.json.vm                                               | deployment-config.json                               |
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
