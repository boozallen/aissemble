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
