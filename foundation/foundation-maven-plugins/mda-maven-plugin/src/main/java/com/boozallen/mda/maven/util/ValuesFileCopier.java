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

import com.boozallen.aiops.mda.generator.common.PipelineImplementationEnum;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import com.boozallen.mda.maven.ArtifactType;
import com.boozallen.mda.maven.PipelineType;
import com.boozallen.mda.maven.mojo.PipelineArtifactsMojo;

import java.nio.file.Files;
import java.nio.file.Path;


/**
 * Copies the values files for the given pyspark pipeline.
 */
public class ValuesFileCopier extends PipelineArtifactCopier {
    private PipelineImplementationEnum pipelineImpl;
    private RetrieveMethod retrieveMethod;

    public ValuesFileCopier(Pipeline pipeline, PipelineImplementationEnum pipelineImpl, RetrieveMethod retrieveMethod) {
        super(pipeline, PipelineType.DATA_FLOW, ArtifactType.VALUES_FILES);
        this.pipelineImpl = pipelineImpl;
        this.retrieveMethod = retrieveMethod;
    }

    @Override
    public void doCopyArtifact(PipelineArtifactsMojo mojo) throws Exception {
        String pipelineName = pipeline.getName();
        Path tempDirectory = Files.createTempDirectory(pipelineName + "-archive");
        Path archive = retrieveMethod.getArchive(mojo, pipelineName, tempDirectory);
        logger.info("Extracting values files from archive [{}]", archive.getFileName());
        mojo.retrieveSparkApplications(pipelineName, archive, pipelineImpl);
    }

    public interface RetrieveMethod {
        Path getArchive(PipelineArtifactsMojo mojo, String pipelineName, Path destination) throws Exception;
    }
}
