package com.boozallen.aiops.mda.generator;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.metamodel.element.Step;
import com.boozallen.aiops.mda.metamodel.element.python.PythonInferenceStep;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Generates Python files that support the inference step of the specific targeted pipeline.
 */
public class TargetedPipelinePythonInferenceGenerator extends BaseTargetedPipelineInferenceGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                          | Template                                | Generated File                                             |
     * |---------------------------------|-----------------------------------------|------------------------------------------------------------|
     * | inferencePayloadDefinitionBase  | inference/inference.payload.base.py.vm  | generated/validation/inference_payload_definition_base.py  |
     */


    @Override
    protected List<? extends Step> decorateInferenceSteps(List<Step> inferenceSteps) {
        return inferenceSteps.stream().map(step -> new PythonInferenceStep(step)).collect(Collectors.toList());
    }

    @Override
    protected String getOutputSubFolder() {
        return "";
    }
}
