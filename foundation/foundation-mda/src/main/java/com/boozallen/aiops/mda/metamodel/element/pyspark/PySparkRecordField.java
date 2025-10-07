package com.boozallen.aiops.mda.metamodel.element.pyspark;

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

import com.boozallen.aiops.mda.metamodel.element.RecordField;
import com.boozallen.aiops.mda.metamodel.element.RecordFieldType;
import com.boozallen.aiops.mda.metamodel.element.python.PythonRecordField;
import com.boozallen.aiops.mda.metamodel.element.util.SparkAttributes;

/**
 * Decorates RecordField with PySpark-specific functionality.
 */
public class PySparkRecordField extends PythonRecordField {

    /**
     * {@inheritDoc}
     */
    public PySparkRecordField(RecordField recordFieldToDecorate) {
        super(recordFieldToDecorate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecordFieldType getType() {
        return new PySparkRecordFieldType(wrapped.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getShortType() {
        PySparkRecordFieldType fieldType = (PySparkRecordFieldType) getType();
        PySparkDictionaryType dictionaryType = (PySparkDictionaryType) fieldType.getDictionaryType();

        return dictionaryType.getShortType();
    }

    /**
     * Returns Spark-related attributes for this field.
     * 
     * @return Spark-related attributes
     */
    public SparkAttributes getSparkAttributes() {
        return new SparkAttributes(this);
    }

}
