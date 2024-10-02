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

import com.boozallen.aiops.mda.generator.util.MavenUtil.Language;
import com.boozallen.aiops.mda.generator.util.SemanticDataUtil;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

/**
 * Enables the generation of {@code pom.xml} files in the root project directory of Java data records modules.
 */
public class DataRecordsJavaPomGenerator extends DataRecordsPomGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                                  | Template                                                                        | Generated File                               |
     * |-----------------------------------------|---------------------------------------------------------------------------------|----------------------------------------------|
     * | dataDeliveryDataRecordsJavaPomFile      | data-delivery-data-records/data-delivery-combined-data-records-java.pom.xml.vm  | ${project}data-records${lang}/pom.xml        |
     * | dataDeliverySparkDataPomFileJava        | data-delivery-data-records/data-delivery-data-spark.pom.xml.vm                  | ${project}data-records-spark${lang}/pom.xml  |
     * | dataDeliveryCoreDataRecordsPomFileJava  | data-delivery-data-records/data-delivery-separate-data-records-java.pom.xml.vm  | ${project}data-records-core${lang}/pom.xml   |
     */


    @Override
    protected boolean shouldGenerate() {
        return SemanticDataUtil.areJavaDataRecordsNeeded(metadataContext);
    }

    @Override
    protected void populateVelocityContext(GenerationContext context, VelocityContext vc) {
    }

    @Override
    protected Language getLanguage() {
        return Language.JAVA;
    }
}
