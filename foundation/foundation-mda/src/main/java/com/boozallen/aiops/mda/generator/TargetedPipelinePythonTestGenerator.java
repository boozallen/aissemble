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

import com.boozallen.aiops.mda.generator.util.PipelineUtils;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import com.boozallen.aiops.mda.metamodel.element.python.PythonPipeline;
import java.util.Map;

import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

/**
 * Generation for Behave test files, for the target pipeline defined the fermenter-mda plugin.
 */
public class TargetedPipelinePythonTestGenerator extends AbstractPythonGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                        | Template                                             | Generated File           |
     * |-------------------------------|------------------------------------------------------|--------------------------|
     * | pySparkBehaveEnvironmentBase  | data-delivery-pyspark/behave.environment.base.py.vm  | environment_base.py      |
     * | pySparkBehaveEnvironment      | data-delivery-pyspark/behave.environment.py.vm       | features/environment.py  |
     */


    @Override
    public void generate(GenerationContext generationContext) {
        Pipeline pipeline = PipelineUtils.getTargetedPipeline(generationContext, metadataContext);
        PythonPipeline pythonTargetPipeline = new PythonPipeline(pipeline);

        Map<String, String> generationPropertyVariables = generationContext.getPropertyVariables();
        String behaveFeature = generationPropertyVariables.get("behaveFeature");

        VelocityContext vc = getNewVelocityContext(generationContext);
        vc.put(VelocityProperty.PIPELINE, pythonTargetPipeline);
        vc.put(VelocityProperty.BEHAVE_FEATURE, behaveFeature);
        vc.put(VelocityProperty.ARTIFACT_ID, pythonTargetPipeline.deriveArtifactIdFromCamelCase());
        vc.put(VelocityProperty.ARTIFACT_ID_PYTHON_CASE, pythonTargetPipeline.getSnakeCaseName());


        String baseOutputFile = generationContext.getOutputFile();
        String fileName = replace("behaveFeature", baseOutputFile, behaveFeature);
        generationContext.setOutputFile(fileName);

        generateFile(generationContext, vc);
    }
}
