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
