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
