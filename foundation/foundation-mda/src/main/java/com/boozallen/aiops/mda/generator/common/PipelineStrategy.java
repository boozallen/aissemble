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

import com.boozallen.aiops.mda.metamodel.element.Pipeline;

import java.util.List;

public interface PipelineStrategy {

    List<Pipeline> getPipelines();

    List<String> getArtifactIds();

    List<String> getStepArtifactIds();

    List<PipelineStepPair> getSteps();

}
