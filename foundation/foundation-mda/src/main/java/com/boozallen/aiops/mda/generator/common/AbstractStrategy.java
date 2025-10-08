package com.boozallen.aiops.mda.generator.common;

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

import java.util.ArrayList;
import java.util.List;

import com.boozallen.aiops.mda.generator.util.PipelineUtils;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;

/**
 * Abstract strategy class for common method implementations.
 */
public abstract class AbstractStrategy implements PipelineStrategy {

    protected final List<Pipeline> pipelines;

    protected AbstractStrategy(List<Pipeline> pipelines, PipelineEnum pipelineType) {
        this.pipelines = getPipelinesByType(pipelines, pipelineType);
    }

    private List<Pipeline> getPipelinesByType(List<Pipeline> pipelines, PipelineEnum pipelineType) {
        List<Pipeline> pipelinesByType = new ArrayList<>();
        if (pipelines != null) {
            for (Pipeline pipeline : pipelines) {
                if (pipelineType.equalsIgnoreCase(pipeline.getType().getName())) {
                    pipelinesByType.add(pipeline);
                }
            }
        } else {
            throw new IllegalArgumentException("Attempting to read a null pipelines list");
        }

        return pipelinesByType;
    }

    /**
     * This method gets the pipelines that pertain to the strategy.
     * 
     * @return list of pipelines
     */
    @Override
    public List<Pipeline> getPipelines() {
        return pipelines;
    }

    /**
     * This method gets all pipeline artifacts ids.
     * 
     * @return list of pipeline artifact ids
     */
    @Override
    public List<String> getArtifactIds() {
        List<String> pipelineArtifactIds = new ArrayList<>();

        for (Pipeline pipeline : pipelines) {
            String pipelineName = pipeline.getName();
            String artifactId = PipelineUtils.deriveArtifactIdFromCamelCase(pipelineName);
            pipelineArtifactIds.add(artifactId);
        }

        return pipelineArtifactIds;
    }

    /**
     * This method gets all step artifacts across all pipelines.
     * 
     * @return list of step artifact ids
     */
    @Override
    public List<String> getStepArtifactIds() {
        List<String> stepArtifactIds = new ArrayList<>();

        for (PipelineStepPair pair : getSteps()) {
            String pipelineStepName = pair.getStep().getName();
            String artifactId = PipelineUtils.deriveArtifactIdFromCamelCase(pipelineStepName);
            stepArtifactIds.add(artifactId);
        }

        return stepArtifactIds;
    }

}
