package com.boozallen.mda.maven.util;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Maven Plugins::MDA Maven Plugin
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.mda.maven.ArtifactType;
import com.boozallen.mda.maven.PipelineType;
import com.boozallen.mda.maven.mojo.PipelineArtifactsMojo;

/**
 * Represents the artifact(s) targeted by a copier.
 */
public class CopierTarget {
    private final PipelineType pipelineType;
    private final ArtifactType artifactType;

    public CopierTarget(PipelineType pipelineType, ArtifactType artifactType) {
        this.pipelineType = pipelineType;
        this.artifactType = artifactType;
    }

    public ArtifactType getArtifactType() {
        return artifactType;
    }

    public PipelineType getPipelineType() {
        return pipelineType;
    }

    public boolean isTargeted(PipelineArtifactsMojo mojo) {
        return isPipelineTargeted(mojo) && isArtifactTargeted(mojo);
    }

    private boolean isPipelineTargeted(PipelineArtifactsMojo mojo) {
        return pipelineType == null || mojo.isTargeting(pipelineType);
    }

    private boolean isArtifactTargeted(PipelineArtifactsMojo mojo) {
        return artifactType == null || mojo.isTargeting(artifactType);
    }
}
