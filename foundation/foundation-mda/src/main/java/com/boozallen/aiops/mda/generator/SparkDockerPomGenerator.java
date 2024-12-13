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

import com.boozallen.aiops.mda.generator.common.DataFlowStrategy;
import com.boozallen.aiops.mda.generator.common.MachineLearningStrategy;
import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.generator.util.SemanticDataUtil;
import com.boozallen.aiops.mda.metamodel.AissembleModelInstanceRepository;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Iterates through each pipeline in the metamodel and generates the spark
 * docker module if a data-flow pipeline exists.
 */
public class SparkDockerPomGenerator extends AbstractMavenModuleGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                    | Template                                       | Generated File               |
     * |---------------------------|------------------------------------------------|------------------------------|
     * | sparkWorkerDockerPomFile  | general-docker/spark-worker.docker.pom.xml.vm  | ${moduleArtifactId}/pom.xml  |
     */


    /**
     * {@inheritDoc}
     */
    @Override
    public void generate(GenerationContext context) {
        AissembleModelInstanceRepository metamodelRepository = (AissembleModelInstanceRepository) context.getModelInstanceRepository();

        Map<String, Pipeline> pipelineMap = metamodelRepository.getPipelinesByContext(metadataContext);

        String fileName;
        String basefileName = context.getOutputFile();
        basefileName = replaceBasePackage(basefileName, context.getBasePackageAsPath());

        // Get the data-flow pipelines
        List<Pipeline> pipelines = new ArrayList<>(pipelineMap.values());
        DataFlowStrategy dataFlowStrategy = new DataFlowStrategy(pipelines);
        List<Pipeline> dataFlowPipelines = dataFlowStrategy.getPipelines();

        // only generate if there is a data-flow pipeline
        if (!dataFlowPipelines.isEmpty()) {
            VelocityContext vc = new VelocityContext();
            String rootArtifactId = context.getRootArtifactId();
            String sparkDockerArtifactId = context.getArtifactId().replace("-docker", "-spark-worker-docker");
            String pipelinesArtifactId = context.getArtifactId().replace("-docker", "-pipelines");

            vc.put(VelocityProperty.ROOT_ARTIFACT_ID, rootArtifactId);
            vc.put(VelocityProperty.GROUP_ID, context.getGroupId());
            vc.put(VelocityProperty.PARENT_ARTIFACT_ID, context.getArtifactId());
            vc.put(VelocityProperty.VERSION, context.getVersion());
            vc.put(VelocityProperty.PARENT_DESCRIPTIVE_NAME, context.getDescriptiveName());
            vc.put(VelocityProperty.ARTIFACT_ID, sparkDockerArtifactId);

            vc.put(VelocityProperty.PIPELINE_ARTIFACT_IDS, dataFlowStrategy.getArtifactIds());
            vc.put(VelocityProperty.SPARK_PIPELINES, dataFlowStrategy.getSparkPipelines());
            vc.put(VelocityProperty.PYSPARK_PIPELINES, dataFlowStrategy.getPySparkPipelines());
            vc.put(VelocityProperty.ENABLE_PYSPARK_SUPPORT, dataFlowStrategy.isPySparkSupportNeeded());

            // Postgres support is determined by checking both data-flow and machine-learning pipelines
            MachineLearningStrategy machineLearningStrategy = new MachineLearningStrategy(pipelines);
            boolean postgresSupportIsNeeded = dataFlowStrategy.isPostgresSupportNeeded() ||
                    machineLearningStrategy.isPostgresSupportNeeded() ||
                    dataFlowStrategy.isRdbmsSupportNeeded() ||
                    machineLearningStrategy.isRdbmsSupportNeeded();
            vc.put(VelocityProperty.ENABLE_POSTGRES_SUPPORT, postgresSupportIsNeeded);
            vc.put(VelocityProperty.ENABLE_RDBMS_SUPPORT, postgresSupportIsNeeded);

            vc.put(VelocityProperty.ENABLE_SEDONA_SUPPORT, dataFlowStrategy.isSedonaSupportNeeded());
            vc.put(VelocityProperty.ENABLE_SEMANTIC_DATA_SUPPORT, SemanticDataUtil.hasSemanticDataByContext(context, metadataContext));

            fileName = replace(VelocityProperty.MODULE_ARTIFACT_ID, basefileName, sparkDockerArtifactId);
            context.setOutputFile(fileName);

            generateFile(context, vc);

            final String sparkOperatorAppName = "spark-operator";
            final String sparkInfrastructureAppName = "spark-infrastructure";

            manualActionNotificationService.addNoticeToAddModuleToParentBuild(context, sparkDockerArtifactId, "docker");
            manualActionNotificationService.addDeployPomMessage(context, "aissemble-spark-operator-deploy-v2", sparkOperatorAppName);
            manualActionNotificationService.addDeployPomMessage(context, "aissemble-spark-infrastructure-deploy-v2", sparkInfrastructureAppName);
            manualActionNotificationService.addSparkApplicationTiltMessage(context);
            String pipelineArtifactId;
            for (Pipeline pipeline: dataFlowPipelines) {
                pipelineArtifactId = deriveArtifactIdFromCamelCase(pipeline.getName());
                manualActionNotificationService.addSparkWorkerTiltResources(context, pipelinesArtifactId,
                        pipelineArtifactId, pipeline.getType().getImplementation());
            }

            if (dataFlowStrategy.isMetadataNeeded()) {
                manualActionNotificationService.addDeployPomMessage(context, "metadata-deploy-v2", "metadata");
            }
            if (dataFlowStrategy.isElasticsearchSupportNeeded()) {
                manualActionNotificationService.addDeployPomMessage(context, "elasticsearch-operator-deploy-v2", "elasticsearch-operator");
                manualActionNotificationService.addDeployPomMessage(context, "elasticsearch-deploy-v2", "elasticsearch");
                manualActionNotificationService.addElasticsearchTiltResources(context, "elasticsearch", context.getArtifactId().replace("-docker", ""));
            }
            if (dataFlowStrategy.isNeo4jSupportNeeded()) {
                manualActionNotificationService.addDeployPomMessage(context, "neo4j-deploy", "neo4j");
            }
            if (dataFlowStrategy.isPostgresSupportNeeded() || dataFlowStrategy.isRdbmsSupportNeeded()) {
                manualActionNotificationService.addDeployPomMessage(context, "postgres-deploy", "postgres");
            }
        }
    }
}
