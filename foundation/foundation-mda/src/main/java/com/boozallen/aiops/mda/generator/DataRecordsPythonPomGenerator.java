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
import com.boozallen.aiops.mda.generator.util.MavenUtil.Language;
import com.boozallen.aiops.mda.generator.util.PipelineUtils;
import com.boozallen.aiops.mda.generator.util.SemanticDataUtil;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

/**
 * Generates the {@code pyproject.toml} file in the root of the data-records module.
 */
public class DataRecordsPythonPomGenerator extends DataRecordsPomGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                                    | Template                                                                          | Generated File                               |
     * |-------------------------------------------|-----------------------------------------------------------------------------------|----------------------------------------------|
     * | dataDeliveryDataRecordsPythonPomFile      | data-delivery-data-records/data-delivery-combined-data-records-python.pom.xml.vm  | ${project}data-records${lang}/pom.xml        |
     * | dataDeliverySparkDataPomFilePython        | data-delivery-data-records/data-delivery-data-pyspark.pom.xml.vm                  | ${project}data-records-spark${lang}/pom.xml  |
     * | dataDeliveryCoreDataRecordsPomFilePython  | data-delivery-data-records/data-delivery-separate-data-records-python.pom.xml.vm  | ${project}data-records-core${lang}/pom.xml   |
     */


    @Override
    protected boolean shouldGenerate(GenerationContext generationContext) {
        return SemanticDataUtil.arePythonDataRecordsNeeded(generationContext, metadataContext);
    }

    @Override
    protected void populateVelocityContext(GenerationContext context, VelocityContext vc) {
        String artifactId = (String) vc.get(VelocityProperty.ARTIFACT_ID);
        vc.put(VelocityProperty.MODULE_ARTIFACT_ID_PYTHON_CASE,
                PipelineUtils.deriveLowerSnakeCaseNameFromHyphenatedString(artifactId));
    }

    @Override
    protected Language getLanguage() {
        return Language.PYTHON;
    }
}
