package com.boozallen.aiops.mda.generator;

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
