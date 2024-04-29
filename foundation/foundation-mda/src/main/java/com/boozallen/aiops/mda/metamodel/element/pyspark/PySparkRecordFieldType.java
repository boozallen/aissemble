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

import com.boozallen.aiops.mda.metamodel.element.DictionaryType;
import com.boozallen.aiops.mda.metamodel.element.RecordFieldType;
import com.boozallen.aiops.mda.metamodel.element.python.PythonRecordFieldType;

/**
 * Decorates RecordFieldType with PySpark-specific functionality.
 */
public class PySparkRecordFieldType extends PythonRecordFieldType {

    /**
     * {@inheritDoc}
     */
    public PySparkRecordFieldType(RecordFieldType recordFieldTypeToDecorate) {
        super(recordFieldTypeToDecorate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DictionaryType getDictionaryType() {
        return new PySparkDictionaryType(wrapped.getDictionaryType());
    }

}
