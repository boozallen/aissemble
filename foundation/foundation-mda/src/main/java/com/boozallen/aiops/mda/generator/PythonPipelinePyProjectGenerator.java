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

