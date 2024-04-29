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

import com.boozallen.aiops.mda.DockerBuildParams;
import com.boozallen.aiops.mda.generator.common.DataFlowStrategy;
import com.boozallen.aiops.mda.generator.common.MachineLearningStrategy;
import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.metamodel.AIOpsModelInstanceRepostory;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceRepositoryManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Iterates through each pipeline in the metamodel and generates the
 * Airflow docker module if there is an ML pipeline.
 */
public class AirflowDockerPomGenerator extends AbstractMavenModuleGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                | Template                                  | Generated File               |
     * |-----------------------|-------------------------------------------|------------------------------|
     * | airflowDockerPomFile  | general-docker/airflow.docker.pom.xml.vm  | ${moduleArtifactId}/pom.xml  |
     */

    private static final String MODULE_ARTIFACT_ID = "moduleArtifactId";

    /**
     * {@inheritDoc}
     */
    @Override
    public void generate(GenerationContext context) {
        AIOpsModelInstanceRepostory metamodelRepository = ModelInstanceRepositoryManager
                .getMetamodelRepository(AIOpsModelInstanceRepostory.class);

        Map<String, Pipeline> pipelineMap = metamodelRepository.getPipelinesByContext(metadataContext);

        String fileName;
        String basefileName = context.getOutputFile();
        basefileName = replaceBasePackage(basefileName, context.getBasePackageAsPath());

        // Get the ML pipelines
        List<Pipeline> pipelines = new ArrayList<>(pipelineMap.values());
        DataFlowStrategy dataFlowStrategy = new DataFlowStrategy(pipelines);
        MachineLearningStrategy mlStrategy = new MachineLearningStrategy(pipelines);
        
        // Only generate if there is an ML pipeline and airflow is needed
       
        if (mlStrategy.isAirflowNeeded() || dataFlowStrategy.isAirflowNeeded()) {
            VelocityContext vc = new VelocityContext();
            String rootArtifactId = context.getRootArtifactId();
            String airflowDockerArtifactId = context.getArtifactId().replace("-docker", "-airflow-docker");

            vc.put(VelocityProperty.ROOT_ARTIFACT_ID, rootArtifactId);
            vc.put(VelocityProperty.ARTIFACT_ID, airflowDockerArtifactId);
            vc.put(VelocityProperty.GROUP_ID, context.getGroupId());
            vc.put(VelocityProperty.PARENT_ARTIFACT_ID, context.getArtifactId());
            vc.put(VelocityProperty.VERSION, context.getVersion());
            vc.put(VelocityProperty.PARENT_DESCRIPTIVE_NAME, context.getDescriptiveName());
            vc.put(VelocityProperty.TRAINING_PIPELINES, mlStrategy.getSteps());
            vc.put(VelocityProperty.SPARK_PIPELINES, dataFlowStrategy.getSparkPipelines());
            vc.put(VelocityProperty.PYSPARK_PIPELINES, dataFlowStrategy.getPySparkPipelines());
            vc.put(VelocityProperty.ENABLE_PYSPARK_SUPPORT, dataFlowStrategy.isPySparkSupportNeeded());
            vc.put(VelocityProperty.ENABLE_DELTA_SUPPORT, dataFlowStrategy.isDeltaSupportNeeded());
            vc.put(VelocityProperty.ENABLE_HIVE_SUPPORT, dataFlowStrategy.isHiveSupportNeeded());
            vc.put(VelocityProperty.ENABLE_POSTGRES_SUPPORT, dataFlowStrategy.isPostgresSupportNeeded());
            vc.put(VelocityProperty.ENABLE_ELASTICSEARCH_SUPPORT, dataFlowStrategy.isElasticsearchSupportNeeded());
            vc.put(VelocityProperty.ENABLE_NEO4J_SUPPORT, dataFlowStrategy.isNeo4jSupportNeeded());
            //get all the data flow pipelines that require airflow so we can generate dags for those too
            vc.put(VelocityProperty.DATAFLOW_PIPELINES, dataFlowStrategy.getDataFlowPipelinesRequiringAirflow());
            
            fileName = replace(MODULE_ARTIFACT_ID, basefileName, airflowDockerArtifactId);
            context.setOutputFile(fileName);

            generateFile(context, vc);

            manualActionNotificationService.addNoticeToAddModuleToParentBuild(context, airflowDockerArtifactId, "docker");
            DockerBuildParams params = new DockerBuildParams.ParamBuilder()
                    .setContext(context)
                    .setAppName("airflow")
                    .setDockerApplicationArtifactId(airflowDockerArtifactId)
                    .setDockerArtifactId(context.getArtifactId())
                    .setIncludeLatestTag(true).build();
            manualActionNotificationService.addDockerBuildTiltFileMessage(params);
            manualActionNotificationService.addDeployPomMessage(context,"airflow-deploy-v2", "airflow");
            manualActionNotificationService.addDeployPomMessage(context, "aissemble-shared-infrastructure-deploy", "shared-infrastructure");
        }
    }


}
