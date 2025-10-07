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

import com.boozallen.aiops.mda.generator.common.DataFlowStrategy;
import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.metamodel.AissembleModelInstanceRepository;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import com.boozallen.aiops.mda.metamodel.element.BasePipelineDecorator;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

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
        AissembleModelInstanceRepository metamodelRepository = (AissembleModelInstanceRepository) generationContext.getModelInstanceRepository();

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
