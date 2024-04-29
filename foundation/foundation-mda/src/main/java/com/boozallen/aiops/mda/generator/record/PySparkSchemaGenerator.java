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
import com.boozallen.aiops.mda.metamodel.element.pyspark.PySparkSchemaRecord;
import com.boozallen.aiops.mda.metamodel.element.python.PythonRecord;

/**
 * Iterates through each record in the metamodel and enables the generation of a
 * pyspark-schema file for each record.
 */
public class PySparkSchemaGenerator extends PythonRecordGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                 | Template                                              | Generated File                       |
     * |------------------------|-------------------------------------------------------|--------------------------------------|
     * | pySparkSchemaBase      | data-delivery-data-records/pyspark.schema.base.py.vm  | schema/${recordName}_schema_base.py  |
     * | pySparkSchemaImpl      | data-delivery-data-records/pyspark.schema.impl.py.vm  | schema/${recordName}_schema.py       |
     * | pySparkSchemaBaseInit  | python.init.py.vm                                     | schema/__init__.py                   |
     * | pySparkSchemaImplInit  | python.init.py.vm                                     | schema/__init__.py                   |
     */


    @Override
    protected PythonRecord getPythonRecord(Record currentRecord) {
        return new PySparkSchemaRecord(currentRecord);
    }

}
