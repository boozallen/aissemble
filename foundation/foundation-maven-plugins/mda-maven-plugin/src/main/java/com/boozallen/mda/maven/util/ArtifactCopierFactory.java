package com.boozallen.mda.maven.util;

/*-
 * #%L
 * MDA Maven::Plugin
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.generator.common.PipelineImplementationEnum;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import com.boozallen.mda.maven.PipelineType;
import com.boozallen.mda.maven.mojo.PipelineArtifactsMojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Returns the appropriate pipeline artifact copiers based on the {@link Pipeline} type and implementation.
 */
public class ArtifactCopierFactory {
    public List<ArtifactCopier> makeCopiers(Pipeline pipeline, List<String> dataRecordModules) {
        List<ArtifactCopier> artifactCopiers = new ArrayList<>();

        if (pipeline.getType().getName().equals("data-flow")) {
            if (pipeline.getType().getImplementation().equals("data-delivery-spark")) {
                artifactCopiers.add(new JarCopier(pipeline));
                artifactCopiers.add(new ValuesFileCopier(pipeline, PipelineImplementationEnum.DATA_DELIVERY_SPARK, PipelineArtifactsMojo::retrieveSparkJar));
            } else if (pipeline.getType().getImplementation().equals("data-delivery-pyspark")) {
                artifactCopiers.add(new TarballCopier(pipeline));
                artifactCopiers.add(new WheelCopier(pipeline));
                artifactCopiers.add(new RequirementsCopier(pipeline));
                artifactCopiers.add(new ValuesFileCopier(pipeline, PipelineImplementationEnum.DATA_DELIVERY_PYSPARK, PipelineArtifactsMojo::retrieveWheel));
                for (String eachModule : dataRecordModules) {
                    artifactCopiers.add(new PythonDataCopier(eachModule));
                }
            } else {
                throw new RuntimeException("Unrecognized data delivery implementation: " + pipeline.getType().getImplementation());
            }
        } else if (pipeline.getType().getName().equals("machine-learning")) {
            artifactCopiers.add(new MachineLearningTrainingCopier(pipeline, PipelineType.ML_TRAINING, "training"));
            artifactCopiers.add(new MachineLearningTrainingCopier(pipeline, PipelineType.SAGEMAKER_TRAINING, "sagemaker-training"));
        } else {
            throw new RuntimeException("Unrecognized pipeline type: " + pipeline.getType().getName());
        }

        return artifactCopiers;
    }
}
