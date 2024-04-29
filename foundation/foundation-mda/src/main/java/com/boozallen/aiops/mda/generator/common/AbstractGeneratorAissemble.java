package com.boozallen.aiops.mda.generator.common;

/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.generator.util.PipelineUtils;
import org.technologybrewery.fermenter.mda.generator.AbstractGenerator;

public abstract class AbstractGeneratorAissemble extends AbstractGenerator {

    @Override
    protected String deriveArtifactIdFromCamelCase(String camelCasedString) {
        return PipelineUtils.deriveArtifactIdFromCamelCase(camelCasedString);
    }

}
