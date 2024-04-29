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
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Copies the artifacts of the given data record module.
 */
public class PythonDataCopier extends ArtifactCopier {
    private final String dataModule;

    public PythonDataCopier(String dataModule) {
        super(PipelineType.DATA_FLOW, ArtifactType.TARBALL);
        this.dataModule = dataModule;
    }

    @Override
    public void copyArtifact(PipelineArtifactsMojo mojo) {
        try {
            mojo.getDataModuleArtifact(dataModule);
        } catch (MojoExecutionException e) {
            throw new RuntimeException("Failed to copy data record module: " + dataModule, e);
        }
    }

    @Override
    public boolean isTargeted(PipelineArtifactsMojo mojo) {
        return super.isTargeted(mojo) && mojo.hasSemanticData();
    }
}
