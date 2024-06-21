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

import com.boozallen.aiops.mda.generator.common.PersistType;
import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.generator.util.PythonGeneratorUtils;
import com.boozallen.aiops.mda.generator.util.SemanticDataUtil;
import com.boozallen.aiops.mda.generator.util.SemanticDataUtil.DataRecordModule;
import com.boozallen.aiops.mda.metamodel.AIOpsModelInstanceRepostory;
import com.boozallen.aiops.mda.metamodel.element.BasePipelineDecorator;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceRepositoryManager;

import java.util.Map;

/**
 * Iterates through each pipeline in the metamodel and enables the generation of
 * a Maven module for each pipeline.
 */
public class PipelineModuleGenerator extends AbstractMavenModuleGenerator {
    /*--~-~-~~
     * Usages:
     * | Target           | Template                                       | Generated File               |
     * |------------------|------------------------------------------------|------------------------------|
     * | pipelinePomFile  | ${pipelineImplementation}/pipeline.pom.xml.vm  | ${moduleArtifactId}/pom.xml  |
     */


    /**
     * {@inheritDoc}
     */
    @Override
    public void generate(GenerationContext context) {
        AIOpsModelInstanceRepostory metamodelRepository = ModelInstanceRepositoryManager
                .getMetamodelRepository(AIOpsModelInstanceRepostory.class);

        Map<String, Pipeline> pipelineMap = metamodelRepository.getPipelinesByContext(metadataContext);

        String fileName;
        String originalTemplateName = context.getTemplateName();
        String basefileName = context.getOutputFile();
        basefileName = replaceBasePackage(basefileName, context.getBasePackageAsPath());

        if (pipelineMap.isEmpty()) {
            manualActionNotificationService.addNoticeToAddPipelines(context);
        }

        for (Pipeline pipeline : pipelineMap.values()) {
            VelocityContext vc = new VelocityContext();

            String pipelineName = pipeline.getName();
            vc.put(VelocityProperty.GROUP_ID, context.getGroupId());
            vc.put(VelocityProperty.PARENT_ARTIFACT_ID, context.getArtifactId());
            vc.put(VelocityProperty.VERSION, context.getVersion());
            vc.put(VelocityProperty.BASE_PACKAGE, context.getBasePackage());
            String artifactId = deriveArtifactIdFromCamelCase(pipelineName);
            vc.put(VelocityProperty.MODULE_ARTIFACT_ID, artifactId);
            vc.put(VelocityProperty.MODULE_ARTIFACT_ID_PYTHON_CASE, PythonGeneratorUtils.normalizeToPythonCase(artifactId));
            vc.put(VelocityProperty.PARENT_DESCRIPTIVE_NAME, context.getDescriptiveName());
            String descriptiveName = deriveDescriptiveNameFromCamelCase(pipelineName);
            vc.put(VelocityProperty.DESCRIPTIVE_NAME, descriptiveName);
            vc.put(VelocityProperty.PIPELINE, pipeline);

            BasePipelineDecorator pipelineDecorator = new BasePipelineDecorator(pipeline);
            boolean enableHiveSupport = pipelineDecorator.isHiveSupportNeeded();
            boolean enableSedonaSupport = pipelineDecorator.isSedonaSupportNeeded();
            boolean enableElasticsearchSupport = pipelineDecorator.isElasticsearchSupportNeeded();
            boolean enableNeo4jSupport = pipelineDecorator.isNeo4jSupportNeeded();
            vc.put(VelocityProperty.ENABLE_HIVE_SUPPORT, enableHiveSupport);
            vc.put(VelocityProperty.ENABLE_SEDONA_SUPPORT, enableSedonaSupport);
            vc.put(VelocityProperty.ENABLE_POSTGRES_SUPPORT, pipelineDecorator.isPostgresSupportNeeded());
            vc.put(VelocityProperty.ENABLE_RDBMS_SUPPORT, pipelineDecorator.isRdbmsSupportNeeded());
            vc.put(VelocityProperty.ENABLE_ELASTICSEARCH_SUPPORT, enableElasticsearchSupport);
            vc.put(VelocityProperty.ENABLE_NEO4J_SUPPORT, enableNeo4jSupport);
            vc.put(VelocityProperty.ENABLE_SEMANTIC_DATA_SUPPORT, SemanticDataUtil.hasSemanticDataByContext(metadataContext));
            vc.put(VelocityProperty.PROJECT_NAME, context.getRootArtifactId());
            vc.put(VelocityProperty.JAVA_DATA_RECORDS, getJavaDataRecordModule(context, DataRecordModule.COMBINED));
            vc.put(VelocityProperty.PYTHON_DATA_RECORDS, getPythonDataRecordModule(context, DataRecordModule.COMBINED));

            fileName = replace(VelocityProperty.MODULE_ARTIFACT_ID, basefileName, artifactId);
            context.setOutputFile(fileName);


            manualActionNotificationService.addNoticeToAddModuleToParentBuild(context, artifactId, "pipeline");

            String pipelineImplementation = pipeline.getType().getImplementation();
            String pipelineSpecificTemplateName = replace("pipelineImplementation", originalTemplateName,
                    pipelineImplementation);
            context.setTemplateName(pipelineSpecificTemplateName);

            generateFile(context, vc);

            // add notice that you need to add new deps for java projects that
            // use postgres or neo4j
            if ("data-delivery-spark".equals(pipeline.getType().getImplementation())) {
                if (pipelineDecorator.isPostgresSupportNeeded()) {
                    manualActionNotificationService.addNoticeToAddDependency(context, artifactId, PersistType.POSTGRES.getValue());
                }
                if (pipelineDecorator.isRdbmsSupportNeeded()) {
                    manualActionNotificationService.addNoticeToAddDependency(context, artifactId, PersistType.RDBMS.getValue());
                }
                if (enableNeo4jSupport) {
                    manualActionNotificationService.addNoticeToAddDependency(context, artifactId, PersistType.NEO4J.getValue());
                }
            }
        }

    }
}
