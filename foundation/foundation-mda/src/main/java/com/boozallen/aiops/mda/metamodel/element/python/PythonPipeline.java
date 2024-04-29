package com.boozallen.aiops.mda.metamodel.element.python;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.generator.util.PipelineUtils;
import com.boozallen.aiops.mda.metamodel.element.BasePipelineDecorator;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;

/**
 * Pipeline decorator to ease generation of Python files.
 */
public class PythonPipeline extends BasePipelineDecorator {

    /**
     * {@inheritDoc}
     */
    public PythonPipeline(Pipeline pipelineToDecorate) {
        super(pipelineToDecorate);

    }

    /**
     * Returns the pipeline name formatted into lowercase with underscores
     * (Python naming convention).
     * 
     * @return the pipeline name formatted into lowercase with underscores
     */
    public String getSnakeCaseName() {
        return PipelineUtils.deriveLowercaseSnakeCaseNameFromCamelCase(getName());
    }

    /**
     * Returns the pipeline name formatted into lowercase with hyphens
     *
     * @return the pipeline name formatted into lowercase with hyphens
     */
    public String getKababCaseName() {
        return PipelineUtils.deriveArtifactIdFromCamelCase(getName());
    }

}
