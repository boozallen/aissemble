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
    protected boolean shouldGenerate(GenerationContext generationContext) {
        return SemanticDataUtil.areJavaDataRecordsNeeded(generationContext, metadataContext);
    }

    @Override
    protected void populateVelocityContext(GenerationContext context, VelocityContext vc) {
    }

    @Override
    protected Language getLanguage() {
        return Language.JAVA;
    }
}
