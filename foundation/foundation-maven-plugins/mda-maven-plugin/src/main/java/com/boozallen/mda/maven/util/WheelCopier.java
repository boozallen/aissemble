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
 * Copies the wheel artifact for the given pyspark pipeline.
 */
public class WheelCopier extends PipelineArtifactCopier {
    public WheelCopier(Pipeline pipeline) {
        super(pipeline, PipelineType.DATA_FLOW, ArtifactType.WHEEL);
    }

    @Override
    public void doCopyArtifact(PipelineArtifactsMojo mojo) throws Exception {
        mojo.retrieveWheel(pipeline.getName());
    }
}
