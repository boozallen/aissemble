package com.boozallen.aiops.mda.generator;

/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.metamodel.AIOpsModelInstanceRepostory;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.boozallen.aiops.mda.generator.util.PythonGeneratorUtils;

import com.boozallen.aiops.mda.generator.common.MachineLearningStrategy;
import com.boozallen.aiops.mda.generator.common.PipelineStepPair;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceRepositoryManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Generates training docker resources if any machine-learning pipelines with an training step exist.
 */
public class SagemakerTrainingDockerResourcesGenerator extends AbstractResourcesGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                       | Template                                          | Generated File     |
     * |------------------------------|---------------------------------------------------|--------------------|
     * | sagemakerTrainingDockerFile  | general-docker/sagemaker-training.docker.file.vm  | docker/Dockerfile  |
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

            AIOpsModelInstanceRepostory metamodelRepository = ModelInstanceRepositoryManager
                .getMetamodelRepository(AIOpsModelInstanceRepostory.class);

            Map<String, Pipeline> pipelineMap = metamodelRepository.getPipelinesByContext(metadataContext);
            List<Pipeline> pipelines = new ArrayList<>(pipelineMap.values());

            MachineLearningStrategy mlStrategy = new MachineLearningStrategy(pipelines);
            List<PipelineStepPair> pipelineStepPairs = mlStrategy.getSagemakerTrainingSteps();
            String trainingPipeline = "TrainingPipeline";
            
            // Is there a better way than this to get the pipeline name?
            for (PipelineStepPair pipelineStepPair : pipelineStepPairs) {
                if (pipelineStepPair.getStepArtifactId().equals(trainingModule)){
                    trainingPipeline = pipelineStepPair.getPipelineArtifactId();
                    break;
                }
            }

            vc.put(VelocityProperty.TRAINING_PIPELINE, trainingPipeline);
            vc.put(VelocityProperty.TRAINING_PIPELINE_SNAKE_CASE, PythonGeneratorUtils.normalizeToPythonCase(trainingPipeline));
            

            generateFile(context, vc);
        }
    }
}
