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

import com.boozallen.aiops.mda.generator.util.PythonGeneratorUtils;
import org.apache.velocity.VelocityContext;
import com.boozallen.aiops.mda.generator.common.AbstractGeneratorAissemble;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

/**
 * Common configuration for generating Python files.
 */
public abstract class AbstractPythonGenerator extends AbstractGeneratorAissemble {

    @Override
    protected VelocityContext getNewVelocityContext(GenerationContext generationContext) {
        VelocityContext velocityContext = super.getNewVelocityContext(generationContext);
        return PythonGeneratorUtils.populateCommonPythonContext(velocityContext, generationContext);
    }

    @Override
    protected String getOutputSubFolder() {
        return "";
    }
}
