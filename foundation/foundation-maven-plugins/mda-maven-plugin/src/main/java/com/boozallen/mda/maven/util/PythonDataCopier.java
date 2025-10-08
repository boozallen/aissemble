package com.boozallen.mda.maven.util;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Maven Plugins::MDA Maven Plugin
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
