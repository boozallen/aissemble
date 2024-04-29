package com.boozallen.aiops.mda.generator.record;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
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
