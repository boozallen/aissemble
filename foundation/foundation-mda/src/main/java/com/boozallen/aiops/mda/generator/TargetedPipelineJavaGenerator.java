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
        vc.put(VelocityProperty.PIPELINE, javaTargetPipeline);
        vc.put(VelocityProperty.ARTIFACT_ID, javaTargetPipeline.deriveArtifactIdFromCamelCase());

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

        if(javaTargetPipeline.hasMessaging()) {
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
            manualActionNotificationService.addNoticeToUpdateKafkaConfig(generationContext, "alerts");
        }
        if(javaTargetPipeline.isMetadataNeeded()) {
            manualActionNotificationService.addNoticeToUpdateKafkaConfig(generationContext, "metadata-ingest");
        }
        if(javaTargetPipeline.getDataLineage()) {
            manualActionNotificationService.addNoticeToUpdateKafkaConfig(generationContext, Constants.DATA_LINEAGE_CHANNEL_NAME);
        }
     }

}
