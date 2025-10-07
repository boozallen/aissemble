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

import static com.boozallen.aiops.mda.generator.util.PipelineUtils.isSynchronousStep;

import com.boozallen.aiops.mda.metamodel.element.Step;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

public class SynchronousStepPythonGenerator extends TargetedPipelineStepPythonGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                           | Template                                                | Generated File        |
     * |----------------------------------|---------------------------------------------------------|-----------------------|
     * | pySparkSynchronousProcessorBase  | data-delivery-pyspark/synchronous.processor.base.py.vm  | step/${name}_base.py  |
     * | pySparkSynchronousProcessorImpl  | data-delivery-pyspark/synchronous.processor.impl.py.vm  | step/${name}.py       |
     */


    @Override
    protected boolean shouldGenerateStep(Step step, GenerationContext generationContext) {
        return isSynchronousStep(step);
    }
}
