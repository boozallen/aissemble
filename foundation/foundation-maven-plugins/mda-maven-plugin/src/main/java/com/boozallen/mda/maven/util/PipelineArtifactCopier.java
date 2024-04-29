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
