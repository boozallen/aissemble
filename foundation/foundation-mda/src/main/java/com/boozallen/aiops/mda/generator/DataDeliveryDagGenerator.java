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
import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.metamodel.AIOpsModelInstanceRepostory;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import com.boozallen.aiops.mda.metamodel.element.BasePipelineDecorator;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceRepositoryManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Iterates through each pipeline in the metamodel and enables the generation of
 * a single DAG file for each data delivery pipeline with airflow as the execution helper.
 */
public class DataDeliveryDagGenerator extends AbstractPythonGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                       | Template                                         | Generated File               |
     * |------------------------------|--------------------------------------------------|------------------------------|
     * | airflowDataDeliveryDagFiles  | deployment/airflow/airflow.data.delivery.dag.vm  | dags/${pipelineName}_dag.py  |
     */

    /**
     * {@inheritDoc}
     */
    @Override
    public void generate(GenerationContext generationContext) {
        AIOpsModelInstanceRepostory metamodelRepository = ModelInstanceRepositoryManager
                .getMetamodelRepository(AIOpsModelInstanceRepostory.class);

        Map<String, Pipeline> pipelineMap = metamodelRepository.getPipelinesByContext(metadataContext);

        String baseOutputFile = generationContext.getOutputFile();
        List<Pipeline> pipelines = new ArrayList<>(pipelineMap.values());

        //Gets the data delivery pipelines that require airflow so we can generate dags for those too
        DataFlowStrategy dataFlowStrategy = new DataFlowStrategy(pipelines);

        for (BasePipelineDecorator pipeline : dataFlowStrategy.getDataFlowPipelinesRequiringAirflow()) {
            VelocityContext vc = getNewVelocityContext(generationContext);
            vc.put(VelocityProperty.PIPELINE, pipeline);

            String fileName = replace("pipelineName", baseOutputFile, pipeline.deriveLowercaseSnakeCaseNameFromCamelCase());
            generationContext.setOutputFile(fileName);
            generateFile(generationContext, vc);
        }
    }
}
