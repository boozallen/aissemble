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

public abstract class ArtifactCopier {
    protected final CopierTarget target;

    public ArtifactCopier(PipelineType pipelineType, ArtifactType artifactType) {
        this.target = new CopierTarget(pipelineType, artifactType);
    }

    public ArtifactType getArtifactType() {
        return target.getArtifactType();
    }

    /**
     * Calls the respective function within the {@link PipelineArtifactsMojo} for copying the given artifact.
     * 
     * @param mojo the executing mojo for copying artifacts
     */
    public abstract void copyArtifact(PipelineArtifactsMojo mojo);

    /**
     * Determines whether the artifact copied by this copier is targeted by the plugin configuration.
     *
     * @param mojo the executing mojo for copying artifacts
     * @return true if this artifact is targeted, false otherwise
     */
    public boolean isTargeted(PipelineArtifactsMojo mojo) {
        return target.isTargeted(mojo);
    }
}
