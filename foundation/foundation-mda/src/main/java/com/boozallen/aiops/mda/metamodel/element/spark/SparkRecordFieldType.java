package com.boozallen.aiops.mda.metamodel.element.spark;

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
import com.boozallen.aiops.mda.metamodel.element.java.JavaRecordFieldType;

/**
 * Decorates RecordFieldType with Spark-specific functionality.
 */
public class SparkRecordFieldType extends JavaRecordFieldType {

    /**
     * {@inheritDoc}
     */
    public SparkRecordFieldType(RecordFieldType recordFieldTypeToDecorate) {
        super(recordFieldTypeToDecorate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DictionaryType getDictionaryType() {
        return new SparkDictionaryType(super.getDictionaryType());
    }

}
