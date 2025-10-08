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
