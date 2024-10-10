package com.boozallen.aiops.mda.generator;

/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.generator.config.deployment.spark.SparkDependencyConfiguration;
import com.boozallen.aiops.mda.generator.util.MavenUtil;
import com.boozallen.aiops.mda.generator.util.MavenUtil.Language;
import com.boozallen.aiops.mda.generator.util.SemanticDataUtil.DataRecordModule;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

/**
 * A generic {@link TargetedPipelinePyProjectGenerator} that enables the generation of {@code pyproject.toml} for
 * modules that do not require any customizations to the functionality provided by the base class.
 */
public class PythonPipelinePyProjectGenerator extends TargetedPipelinePyProjectGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                    | Template                                 | Generated File  |
     * |---------------------------|------------------------------------------|-----------------|
     * | pySparkPyProject          | data-delivery-pyspark/pyproject.toml.vm  | pyproject.toml  |
     * | pythonInferencePyProject  | inference/pyproject.toml.vm              | pyproject.toml  |
     */

    @Override
    protected void doGenerateFile(GenerationContext generationContext, VelocityContext velocityContext, Pipeline pipeline) {
        velocityContext.put(VelocityProperty.PYTHON_DATA_RECORDS, getPythonDataRecordModule(generationContext));
        SparkDependencyConfiguration config = SparkDependencyConfiguration.getInstance();
        velocityContext.put("versionSedona", config.getSedonaVersion());
        generateFile(generationContext, velocityContext);
    }

    private String getPythonDataRecordModule(GenerationContext context) {
        return MavenUtil.getDataRecordModuleName(context, metadataContext, Language.PYTHON, DataRecordModule.COMBINED);
    }

}

