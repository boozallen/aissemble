package com.boozallen.aiops.mda.generator.record;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
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

import com.boozallen.aiops.mda.metamodel.element.Record;
import com.boozallen.aiops.mda.metamodel.element.java.JavaRecord;
import com.boozallen.aiops.mda.metamodel.element.spark.SparkRecord;

/**
 * Iterates through each record in the metamodel and enables the generation of a
 * spark-schema file for each record.
 */
public class SparkSchemaGenerator extends JavaRecordGenerator {
    /*--~-~-~~
     * Usages:
     * | Target           | Template                                              | Generated File                               |
     * |------------------|-------------------------------------------------------|----------------------------------------------|
     * | sparkSchemaBase  | data-delivery-data-records/spark.schema.base.java.vm  | ${basePackage}/${recordName}SchemaBase.java  |
     * | sparkSchemaImpl  | data-delivery-data-records/spark.schema.impl.java.vm  | ${basePackage}/${recordName}Schema.java      |
     */


    @Override
    protected JavaRecord getJavaRecord(Record currentRecord) {
        return new SparkRecord(currentRecord);
    }

}
