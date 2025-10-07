package com.boozallen.aiops.mda.metamodel.element.python;

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

import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import com.boozallen.aiops.mda.metamodel.element.Step;

/**
 * Decorates {@link Pipeline} with machine-learning specific functionality.
 */
public class MachineLearningPipeline extends PythonPipeline {

    public static final String TRAINING_STEP_TYPE = "training";
    public static final String INFERENCE_STEP_TYPE = "inference";

    /**
     * {@inheritDoc}
     */
    public MachineLearningPipeline(Pipeline pipelineToDecorate) {
        super(pipelineToDecorate);
    }

    /**
     * Returns the training step for this machine learning pipeline.
     * 
     * @return training step
     */
    public Step getTrainingStep() {
        return getStepByType(TRAINING_STEP_TYPE);
    }

    /**
     * Returns the inference step for this machine learning pipeline.
     * 
     * @return inference step
     */
    public Step getInferenceStep() {
        return getStepByType(INFERENCE_STEP_TYPE);
    }

}
