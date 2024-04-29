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

import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import com.boozallen.aiops.mda.metamodel.element.Step;

/**
 * Decorates {@link Pipeline} with machine-learning specific functionality.
 */
public class MachineLearningPipeline extends PythonPipeline {

    public static final String TRAINING_STEP_TYPE = "training";
    public static final String SAGEMAKER_TRAINING_STEP_TYPE = "sagemaker-training";
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
     * Returns the training step for this machine learning pipeline.
     * 
     * @return training step
     */
    public Step getSagemakerTrainingStep() {
        return getStepByType(SAGEMAKER_TRAINING_STEP_TYPE);
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
