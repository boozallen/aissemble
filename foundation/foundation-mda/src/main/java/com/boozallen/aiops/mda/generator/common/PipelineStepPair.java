package com.boozallen.aiops.mda.generator.common;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
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
