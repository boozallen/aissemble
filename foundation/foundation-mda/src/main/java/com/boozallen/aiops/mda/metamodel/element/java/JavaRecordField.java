package com.boozallen.aiops.mda.metamodel.element.java;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.metamodel.element.BaseRecordFieldDecorator;
import com.boozallen.aiops.mda.metamodel.element.RecordField;
import com.boozallen.aiops.mda.metamodel.element.RecordFieldType;

/**
 * Decorates RecordField with Java-specific functionality.
 */
public class JavaRecordField extends BaseRecordFieldDecorator {

    /**
     * {@inheritDoc}
     */
    public JavaRecordField(RecordField recordFieldToDecorate) {
        super(recordFieldToDecorate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecordFieldType getType() {
        return new JavaRecordFieldType(super.getType());
    }

}
