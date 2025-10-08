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

import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

import java.util.Map;

/**
 * Generation for test files, for a generic Habushu module.
 */
public class PythonTestGenerator extends AbstractPythonGenerator {
    /*--~-~-~~
     * Usages:
     * | Target         | Template            | Generated File                            |
     * |----------------|---------------------|-------------------------------------------|
     * | behaveFeature  | behave.feature.vm   | features/${behaveFeature}.feature         |
     * | behaveSteps    | behave.steps.py.vm  | features/steps/${behaveFeature}_steps.py  |
     */


    @Override
    public void generate(GenerationContext generationContext) {

        Map<String, String> generationPropertyVariables = generationContext.getPropertyVariables();
        String behaveFeature = generationPropertyVariables.get("behaveFeature");

        VelocityContext vc = getNewVelocityContext(generationContext);
        vc.put(VelocityProperty.BEHAVE_FEATURE, behaveFeature);

        String baseOutputFile = generationContext.getOutputFile();
        String fileName = replace("behaveFeature", baseOutputFile, behaveFeature);
        generationContext.setOutputFile(fileName);

        generateFile(generationContext, vc);
    }
}
