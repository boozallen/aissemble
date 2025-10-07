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
