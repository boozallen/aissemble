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
import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.generator.util.PipelineUtils;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import com.boozallen.aiops.mda.metamodel.element.Step;
import com.boozallen.aiops.mda.metamodel.element.java.JavaPipeline;
import com.boozallen.aiops.mda.metamodel.element.java.JavaStep;
import com.boozallen.aissemble.common.Constants;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows generation for only the pipeline specified by the targetedPipeline property in the fermenter-mda plugin.
 */
public class TargetedPipelineJavaGenerator extends AbstractJavaGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                    | Template                                            | Generated File                                    |
     * |---------------------------|-----------------------------------------------------|---------------------------------------------------|
     * | cucumberSparkBaseHarness  | cucumber.spark.test.base.harness.java.vm            | ${basePackage}/SparkTestBaseHarness.java          |
     * | cucumberSparkHarness      | cucumber.spark.test.impl.harness.java.vm            | ${basePackage}/SparkTestHarness.java              |
     * | javaAbstractPipelineStep  | data-delivery-spark/abstract.pipeline.step.java.vm  | ${basePackage}/AbstractPipelineStep.java          |
     * | sparkPipelineBase         | pipeline.base.java.vm                               | ${basePackage}/pipeline/PipelineBase.java         |
     * | pipelineCdiContextBase    | pipeline.cdi.context.base.java.vm                   | ${basePackage}/cdi/PipelinesCdiContextBase.java   |
     * | pipelineCdiContextImpl    | pipeline.cdi.context.impl.java.vm                   | ${basePackage}/cdi/PipelinesCdiContext.java       |
     * | pipelineDefaultConfig     | pipeline.default.config.java.vm                     | ${basePackage}/${pipelineName}DefaultConfig.java  |
     * | javaPipelineBaseDriver    | pipeline.driver.base.java.vm                        | ${basePackage}/${pipelineName}BaseDriver.java     |
     * | javaPipelineDriver        | pipeline.driver.impl.java.vm                        | ${basePackage}/${pipelineName}Driver.java         |
     */

    public static final String METADATA_CHANNEL = "metadata-ingest";
    public static final String ALERT_CHANNEL = "alerts";

    protected ManualActionNotificationService manualActionNotificationService = new ManualActionNotificationService();

    /**
     * {@inheritDoc}
     */
    @Override
    public void generate(GenerationContext generationContext) {
        super.generate(generationContext);

        VelocityContext vc = getNewVelocityContext(generationContext);

        Pipeline pipeline = PipelineUtils.getTargetedPipeline(generationContext, metadataContext);
        JavaPipeline javaTargetPipeline = new JavaPipeline(pipeline);
        String artifactId = javaTargetPipeline.deriveArtifactIdFromCamelCase();
        vc.put(VelocityProperty.PIPELINE, javaTargetPipeline);
        vc.put(VelocityProperty.ARTIFACT_ID, artifactId);

        String baseOutputFile = generationContext.getOutputFile();
        String fileName = replace("pipelineName", baseOutputFile, javaTargetPipeline.getName());
        generationContext.setOutputFile(fileName);

        List<JavaStep> javaSteps = new ArrayList<>();
        for (Step eachStep : javaTargetPipeline.getSteps()) {
            JavaStep javaStep = new JavaStep(eachStep);
            javaSteps.add(javaStep);
        }
        vc.put(VelocityProperty.STEPS, javaSteps);
        generateFile(generationContext, vc);

        // TODO: Conditional on whether we're specifically using Kafka

        if(javaTargetPipeline.hasMessagingSteps()) {
            for (JavaStep eachStep : javaTargetPipeline.getMessagingSteps()) {
                if (eachStep.hasMessagingInbound()) {
                    manualActionNotificationService.addNoticeToUpdateKafkaConfig(generationContext, eachStep.getInbound().getChannelName());
                }
                if (eachStep.hasMessagingOutbound()) {
                    manualActionNotificationService.addNoticeToUpdateKafkaConfig(generationContext, eachStep.getOutbound().getChannelName());
                }
            }
        }
        if(javaTargetPipeline.isAlertingSupportNeeded()) {
            manualActionNotificationService.addNoticeToUpdateKafkaConfig(generationContext, ALERT_CHANNEL);
            // TODO: StringSerializer actually throws an exception. We need an AlertSerializer in alerting-core
            manualActionNotificationService.addSmallRyeConnectorMessage(generationContext, artifactId, "Alert Producer", ALERT_CHANNEL, "org.apache.kafka.common.serialization.StringSerializer");
        }
        if(javaTargetPipeline.isMetadataNeeded()) {
            manualActionNotificationService.addNoticeToUpdateKafkaConfig(generationContext, METADATA_CHANNEL);
            manualActionNotificationService.addSmallRyeConnectorMessage(generationContext, artifactId, "Metadata Producer", METADATA_CHANNEL, "com.boozallen.aissemble.core.metadata.producer.MetadataSerializer");
        }
        if(javaTargetPipeline.getDataLineage()) {
            manualActionNotificationService.addNoticeToUpdateKafkaConfig(generationContext, Constants.DATA_LINEAGE_CHANNEL_NAME);
            manualActionNotificationService.addSmallRyeConnectorMessage(generationContext, artifactId, "Data Lineage Emitter", Constants.DATA_LINEAGE_CHANNEL_NAME, "org.apache.kafka.common.serialization.StringSerializer");
        }
     }

}
