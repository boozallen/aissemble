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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.boozallen.aiops.mda.generator.util.SemanticDataUtil;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceRepositoryManager;

import com.boozallen.aiops.mda.generator.common.DataFlowStrategy;
import com.boozallen.aiops.mda.generator.common.MachineLearningStrategy;
import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.metamodel.AIOpsModelInstanceRepostory;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;


/**
 * Iterates through each pipeline in the metamodel and enables the generation of
 * docker resources.
 */
public class DockerGenerator extends AbstractResourcesGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                                      | Template                                                           | Generated File                             |
     * |---------------------------------------------|--------------------------------------------------------------------|--------------------------------------------|
     * | airflowDockerFile                           | general-docker/airflow.docker.file.vm                              | docker/Dockerfile                          |
     * | dataAccessDockerFile                        | general-docker/data.access.docker.file.vm                          | docker/Dockerfile                          |
     * | itDockerFile                                | general-docker/it.java.docker.file.vm                              | docker/Dockerfile                          |
     * | jenkinsControllerDockerFile                 | general-docker/jenkins.controller.docker.file.vm                   | docker/Dockerfile                          |
     * | mlflowDockerFile                            | general-docker/mlflow.docker.file.vm                               | docker/Dockerfile                          |
     * | policyDecisionPointAiopsSecurityProperties  | general-docker/policy-decision-point.aiops-security.properties.vm  | krausening/base/aiops-security.properties  |
     * | policyDecisionPointDockerFile               | general-docker/policy.decision.point.docker.file.vm                | docker/Dockerfile                          |
     * | quarkusServiceDockerFile                    | general-docker/quarkus.service.docker.file.vm                      | docker/Dockerfile                          |
     * | sparkWorkerDockerFile                       | general-docker/spark-worker.docker.file.vm                         | docker/Dockerfile                          |
     * | vaultDockerFile                             | general-docker/vault.docker.file.vm                                | docker/Dockerfile                          |
     * | versioningAuthProperties                    | general-docker/versioning.auth.properties.vm                       | krausening/base/auth.properties            |
     * | versioningDockerFile                        | general-docker/versioning.docker.file.vm                           | docker/Dockerfile                          |
     * | versioningMavenSettings                     | general-docker/versioning.docker.maven.settings.xml.vm             | config/settings.xml                        |
     * | versioningModelPomFile                      | general-docker/versioning.docker.model.pom.xml.vm                  | config/model-pom.xml                       |
     */

    /**
     * {@inheritDoc}
     */
    @Override
    public void generate(GenerationContext generationContext) {
        VelocityContext vc = getNewVelocityContext(generationContext);

        AIOpsModelInstanceRepostory metamodelRepository = ModelInstanceRepositoryManager
                .getMetamodelRepository(AIOpsModelInstanceRepostory.class);

        Map<String, Pipeline> pipelineMap = metamodelRepository.getPipelinesByContext(metadataContext);
        List<Pipeline> pipelines = new ArrayList<>(pipelineMap.values());
        vc.put(VelocityProperty.PIPELINES, pipelines);

        // data-flow pipeline properties
        DataFlowStrategy dataFlowStrategy = new DataFlowStrategy(pipelines);
        MachineLearningStrategy machineLearningStrategy = new MachineLearningStrategy(pipelines);
        vc.put(VelocityProperty.SPARK_PIPELINES, dataFlowStrategy.getSparkPipelines());
        vc.put(VelocityProperty.PYSPARK_PIPELINES, dataFlowStrategy.getPySparkPipelines());
        vc.put(VelocityProperty.ENABLE_PYSPARK_SUPPORT, dataFlowStrategy.isPySparkSupportNeeded());
        vc.put(VelocityProperty.ENABLE_DELTA_SUPPORT, dataFlowStrategy.isDeltaSupportNeeded());
        vc.put(VelocityProperty.ENABLE_HIVE_SUPPORT, dataFlowStrategy.isHiveSupportNeeded());
        // Postgres support is determined by checking both data-flow and machine-learning pipelines
        boolean postgresSupportIsNeeded = dataFlowStrategy.isPostgresSupportNeeded() || machineLearningStrategy.isPostgresSupportNeeded();
        boolean rdbmsSupportIsNeeded = dataFlowStrategy.isRdbmsSupportNeeded() || machineLearningStrategy.isRdbmsSupportNeeded();
        vc.put(VelocityProperty.ENABLE_POSTGRES_SUPPORT, postgresSupportIsNeeded);
        vc.put(VelocityProperty.ENABLE_RDBMS_SUPPORT, rdbmsSupportIsNeeded);
        vc.put(VelocityProperty.ENABLE_ELASTICSEARCH_SUPPORT, dataFlowStrategy.isElasticsearchSupportNeeded());
        vc.put(VelocityProperty.ENABLE_NEO4J_SUPPORT, dataFlowStrategy.isNeo4jSupportNeeded());
        vc.put(VelocityProperty.ENABLE_SEDONA_SUPPORT, dataFlowStrategy.isSedonaSupportNeeded());
        vc.put(VelocityProperty.ENABLE_DATA_LINEAGE_SUPPORT, dataFlowStrategy.isDataLineageNeeded());
        vc.put(VelocityProperty.ENABLE_SEMANTIC_DATA_SUPPORT, SemanticDataUtil.hasSemanticDataByContext(metadataContext));

        // machine-learning pipeline properties
        vc.put(VelocityProperty.TRAINING_PIPELINES, machineLearningStrategy.getSteps());

        //get all the data flow pipelines that require airflow so we can copy the jobs within the dockerfile
        vc.put(VelocityProperty.DATAFLOW_PIPELINES, dataFlowStrategy.getDataFlowPipelinesRequiringAirflow());

        String rootArtifactId = generationContext.getRootArtifactId();
        vc.put(VelocityProperty.ROOT_ARTIFACT_ID, rootArtifactId);

        if (shouldGenerate(pipelines)) {
            generateFile(generationContext, vc);
        }
    }

    protected boolean shouldGenerate(List<Pipeline> pipelines) {
        return true;
    }
}
