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
 * Copies the .tar.gz package artifact from the given pyspark pipeline.
 */
public class TarballCopier extends PipelineArtifactCopier {
    public TarballCopier(Pipeline pipeline) {
        super(pipeline, PipelineType.DATA_FLOW, ArtifactType.TARBALL);
    }

    @Override
    public void doCopyArtifact(PipelineArtifactsMojo mojo) throws Exception {
        mojo.retrieveTarball(pipeline.getName());
        //TODO: make this it's own copier
        mojo.retrieveDriver(pipeline.getName());
    }
}
