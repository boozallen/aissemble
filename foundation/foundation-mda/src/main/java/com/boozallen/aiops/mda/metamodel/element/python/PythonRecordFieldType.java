package com.boozallen.aiops.mda.metamodel.element.python;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.metamodel.element.BaseRecordFieldTypeDecorator;
import com.boozallen.aiops.mda.metamodel.element.DictionaryType;
import com.boozallen.aiops.mda.metamodel.element.RecordFieldType;

/**
 * Decorates RecordFieldType with Python-specific functionality.
 */
public class PythonRecordFieldType extends BaseRecordFieldTypeDecorator {

    /**
     * {@inheritDoc}
     */
    public PythonRecordFieldType(RecordFieldType recordFieldTypeToDecorate) {
        super(recordFieldTypeToDecorate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DictionaryType getDictionaryType() {
        return new PythonDictionaryType(super.getDictionaryType());
    }

}
