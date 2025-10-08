package com.boozallen.aiops.mda.generator;

/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
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
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

import static com.boozallen.aiops.mda.generator.util.PipelineUtils.isGenericStep;

public class GenericStepPythonGenerator extends TargetedPipelineStepPythonGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                  | Template                                    | Generated File        |
     * |-------------------------|---------------------------------------------|-----------------------|
     * | pythonGenericStepBase   | pipeline-common/generic.base.py.vm          | ${name}_base.py       |
     * | pySparkGenericStepBase  | pipeline-common/generic.base.py.vm          | step/${name}_base.py  |
     * | pySparkGenericStepImpl  | pipeline-common/generic.pyspark.step.py.vm  | step/${name}.py       |
     * | pythonGenericStepImpl   | pipeline-common/generic.python.step.py.vm   | impl/${name}.py       |
     */

    @Override
    protected boolean shouldGenerateStep(Step step, GenerationContext generationContext) {
        return isGenericStep(step);
    }
}
