package com.boozallen.mda.maven.util;

/*-
 * #%L
 * MDA Maven::Plugin
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
