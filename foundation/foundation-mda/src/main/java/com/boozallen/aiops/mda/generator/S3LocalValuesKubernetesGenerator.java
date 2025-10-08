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

import com.boozallen.aiops.mda.generator.common.PipelineEnum;
import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.metamodel.AissembleModelInstanceRepository;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

import java.util.Map;

public class S3LocalValuesKubernetesGenerator extends AbstractKubernetesGenerator {
    /*--~-~-~~
     * Usages:
     * | Target               | Template                                         | Generated File               |
     * |----------------------|--------------------------------------------------|------------------------------|
     * | s3LocalValuesFileV2  | deployment/localstack/localstack.values.yaml.vm  | apps/${appName}/values.yaml  |
     */

    @Override
    public void generate(GenerationContext context) {
        VelocityContext vc = this.configureWithoutGeneration(context);
        AissembleModelInstanceRepository metamodelRepository = (AissembleModelInstanceRepository) context.getModelInstanceRepository();
        Map<String, Pipeline> pipelineMap = metamodelRepository.getPipelinesByContext(metadataContext);
        for (Pipeline pipeline: pipelineMap.values()) {
            String pipelineType = pipeline.getType().getName();
            if (PipelineEnum.DATA_FLOW.equalsIgnoreCase(pipelineType)) {
                // pyspark/spark pipelines
                vc.put(VelocityProperty.DATAFLOW_PIPELINES, true);
            } else if (PipelineEnum.MACHINE_LEARNING.equalsIgnoreCase(pipelineType)) {
                // training/inference pipelines
                vc.put(VelocityProperty.MACHINE_LEARNING_PIPELINES, true);
            }
        }


        generateFile(context, vc);
    }

}
