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
     * | Target                  | Template                      | Generated File                            |
     * |-------------------------|-------------------------------|-------------------------------------------|
     * | behaveFeatureSagemaker  | behave-sagemaker.feature.vm   | features/${behaveFeature}.feature         |
     * | behaveStepsSagemaker    | behave-sagemaker.steps.py.vm  | features/steps/${behaveFeature}_steps.py  |
     * | behaveFeature           | behave.feature.vm             | features/${behaveFeature}.feature         |
     * | behaveSteps             | behave.steps.py.vm            | features/steps/${behaveFeature}_steps.py  |
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
