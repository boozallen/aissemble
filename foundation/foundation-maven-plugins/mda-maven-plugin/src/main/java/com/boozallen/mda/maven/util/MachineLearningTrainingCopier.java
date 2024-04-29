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

import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import com.boozallen.aiops.mda.metamodel.element.Step;
import com.boozallen.mda.maven.ArtifactType;
import com.boozallen.mda.maven.PipelineType;
import com.boozallen.mda.maven.mojo.PipelineArtifactsMojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Gather pipeline artifacts for machine learning pipelines by making use of the maven resources plugin.
 */
public class MachineLearningTrainingCopier extends PipelineArtifactCopier {
    private static final Logger logger = LoggerFactory.getLogger(MachineLearningTrainingCopier.class);
    private final String stepType;

    public MachineLearningTrainingCopier(Pipeline pipeline, PipelineType pipelineType, String stepType) {
        super(pipeline, pipelineType, ArtifactType.TARBALL);
        this.stepType = stepType;
    }

    @Override
    public void doCopyArtifact(PipelineArtifactsMojo mojo) throws Exception {
        //get the machine learning pipeline training steps
        List<Step> trainingSteps = pipeline.getSteps().stream()
                .filter(step -> step.getType().equals(stepType))
                .collect(Collectors.toList());

        String pipelineName = pipeline.getName();
        for (Step step : trainingSteps) {
            String stepName = step.getName();
            logger.info("Retrieving the artifacts for training step '{}' from machine learning pipeline '{}'", stepName, pipelineName);
            mojo.getMlTrainingStepArtifact(pipelineName, stepName);
        }
    }
}
