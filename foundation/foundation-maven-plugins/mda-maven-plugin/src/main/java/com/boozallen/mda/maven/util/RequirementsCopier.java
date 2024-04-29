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


/**
 * Copies the requirements.txt artifact from the given pyspark pipeline.
 */
public class RequirementsCopier extends PipelineArtifactCopier {
    public RequirementsCopier(Pipeline pipeline) {
        super(pipeline, PipelineType.DATA_FLOW, ArtifactType.REQUIREMENTS);
    }

    @Override
    public void doCopyArtifact(PipelineArtifactsMojo mojo) throws Exception {
        mojo.retrieveRequirements(pipeline.getName());
    }
}
