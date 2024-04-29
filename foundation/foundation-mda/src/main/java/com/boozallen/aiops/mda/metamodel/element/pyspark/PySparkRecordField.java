package com.boozallen.aiops.mda.metamodel.element.pyspark;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
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
