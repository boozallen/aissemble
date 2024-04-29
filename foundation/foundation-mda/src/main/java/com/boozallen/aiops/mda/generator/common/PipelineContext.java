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

import java.util.List;

public class PipelineContext {
    private PipelineStrategy pipelineStrategy;

    public void setPipelineStrategy(PipelineStrategy pipelineStrategy) {
        this.pipelineStrategy = pipelineStrategy;
    }

    public List<String> getArtifactIds() {
        return pipelineStrategy.getArtifactIds();
    }

    public List<String> getStepArtifactIds() {
        return pipelineStrategy.getStepArtifactIds();
    }

    public List<PipelineStepPair> getSteps() {
        return pipelineStrategy.getSteps();
    }
}
