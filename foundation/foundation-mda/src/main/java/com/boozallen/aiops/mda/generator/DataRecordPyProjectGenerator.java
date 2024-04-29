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
import com.boozallen.aiops.mda.generator.util.MavenUtil;
import com.boozallen.aiops.mda.generator.util.MavenUtil.Language;
import com.boozallen.aiops.mda.generator.util.SemanticDataUtil.DataRecordModule;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

/**
 * Enables the generation of {@code pyproject.toml} for Python data records modules.
 *
 * Note: this could be used for any Python module that doesn't target a pipeline, but it is currently only used for data
 * records.
 */
public class DataRecordPyProjectGenerator extends AbstractPyProjectGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                     | Template                                                     | Generated File  |
     * |----------------------------|--------------------------------------------------------------|-----------------|
     * | dataRecordsPyProject       | data-delivery-data-records/combined.pyproject.toml.vm        | pyproject.toml  |
     * | dataRecordsPyProjectCore   | data-delivery-data-records/separate.core.pyproject.toml.vm   | pyproject.toml  |
     * | dataRecordsPyProjectSpark  | data-delivery-data-records/separate.spark.pyproject.toml.vm  | pyproject.toml  |
     */

    @Override
    protected void doGenerateFile(GenerationContext generationContext, VelocityContext velocityContext) {
        velocityContext.put(VelocityProperty.PYTHON_DATA_RECORDS, getPythonDataRecordModule(generationContext));
        generateFile(generationContext, velocityContext);
    }

    private String getPythonDataRecordModule(GenerationContext context) {
        return MavenUtil.getDataRecordModuleName(context, metadataContext, Language.PYTHON, DataRecordModule.CORE);
    }
}
