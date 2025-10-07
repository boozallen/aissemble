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
import com.boozallen.mda.maven.ArtifactType;
import com.boozallen.mda.maven.PipelineType;
import com.boozallen.mda.maven.mojo.PipelineArtifactsMojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copies direct artifacts from a pipeline.
 */
public abstract class PipelineArtifactCopier extends ArtifactCopier {
    protected static final Logger logger = LoggerFactory.getLogger(PipelineArtifactCopier.class);

    protected Pipeline pipeline;

    public PipelineArtifactCopier(Pipeline pipeline, PipelineType pipelineType, ArtifactType artifactType) {
        super(pipelineType, artifactType);
        this.pipeline = pipeline;
    }

    @Override
    public void copyArtifact(PipelineArtifactsMojo mojo) {
        String pipelineName = this.pipeline.getName();
        logger.info("Retrieving '{}' artifact for '{}' pipeline '{}'",
                target.getArtifactType(),
                target.getPipelineType(),
                pipelineName);
        try {
            doCopyArtifact(mojo);
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy " + target.getArtifactType() + " for " + pipelineName, e);
        }
    }

    protected abstract void doCopyArtifact(PipelineArtifactsMojo mojo) throws Exception;
}
