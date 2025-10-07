package com.boozallen.aiops.mda.generator;

/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
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

import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.metamodel.AissembleModelInstanceRepository;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.boozallen.aiops.mda.generator.util.PythonGeneratorUtils;

import com.boozallen.aiops.mda.generator.common.MachineLearningStrategy;
import com.boozallen.aiops.mda.generator.common.PipelineStepPair;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Generates training docker resources if any machine-learning pipelines with an training step exist.
 */
public class TrainingDockerResourcesGenerator extends AbstractResourcesGenerator {
    /*--~-~-~~
     * Usages:
     * | Target               | Template                                | Generated File            |
     * |----------------------|-----------------------------------------|---------------------------|
     * | trainingGitkeepFile  | general-docker/gitkeep.vm               | krausening/base/.gitkeep  |
     * | trainingDockerFile   | general-docker/training.docker.file.vm  | docker/Dockerfile         |
     */


    private static final Logger logger = LoggerFactory.getLogger(TrainingDockerResourcesGenerator.class);

    @Override
    public void generate(GenerationContext context) {
        String rootModuleName = context.getRootArtifactId();

        if (rootModuleName == null || rootModuleName.isEmpty()) {
            logger.error("Root module could not be determined!");
        } else {

            String trainingModule = context.getArtifactId()
                    .replace(rootModuleName + "-", "")
                    .replace("-docker", "");

            VelocityContext vc = getNewVelocityContext(context);
            vc.put(VelocityProperty.TRAINING_MODULE, trainingModule);
            vc.put(VelocityProperty.TRAINING_MODULE_SNAKE_CASE, PythonGeneratorUtils.normalizeToPythonCase(trainingModule));

            AissembleModelInstanceRepository metamodelRepository = (AissembleModelInstanceRepository) context.getModelInstanceRepository();

            Map<String, Pipeline> pipelineMap = metamodelRepository.getPipelinesByContext(metadataContext);
            List<Pipeline> pipelines = new ArrayList<>(pipelineMap.values());

            MachineLearningStrategy mlStrategy = new MachineLearningStrategy(pipelines);
            List<PipelineStepPair> pipelineStepPairs = mlStrategy.getSteps();
            String trainingPipeline = "TrainingPipeline";
            
            // Is there a better way than this to get the pipeline name?
            for (PipelineStepPair pipelineStepPair : pipelineStepPairs) {
                if (pipelineStepPair.getStepArtifactId().equals(trainingModule)){
                    trainingPipeline = pipelineStepPair.getPipelineArtifactId();
                }
            }

            vc.put(VelocityProperty.TRAINING_PIPELINE_SNAKE_CASE, PythonGeneratorUtils.normalizeToPythonCase(trainingPipeline));

            generateFile(context, vc);
        }
    }
}
