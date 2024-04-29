package com.boozallen.aiops.mda.generator.common;

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
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import com.boozallen.aiops.mda.metamodel.element.Step;

public class PipelineStepPair {

    private Pipeline pipeline;
    private Step step;

    public PipelineStepPair(Pipeline pipeline, Step step) {
        this.pipeline = pipeline;
        this.step = step;
    }

    public Pipeline getPipeline() {
        return pipeline;
    }

    public void setPipeline(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }

    /**
     * Returns the artifact id for the pipeline.
     * 
     * @return artifact id for the pipeline
     */
    public String getPipelineArtifactId() {
        return PipelineUtils.deriveArtifactIdFromCamelCase(pipeline.getName());
    }

    /**
     * Returns the artifact id for the step.
     * 
     * @return artifact id for the step.
     */
    public String getStepArtifactId() {
        return PipelineUtils.deriveArtifactIdFromCamelCase(step.getName());
    }

}
