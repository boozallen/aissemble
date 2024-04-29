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
