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

import com.boozallen.aiops.mda.metamodel.element.BaseRecordFieldDecorator;
import com.boozallen.aiops.mda.metamodel.element.RecordField;
import com.boozallen.aiops.mda.metamodel.element.RecordFieldType;
import com.boozallen.aiops.mda.metamodel.element.util.PythonElementUtils;

/**
 * Decorates RecordField with Python-specific functionality.
 */
public class PythonRecordField extends BaseRecordFieldDecorator {

    /**
     * {@inheritDoc}
     */
    public PythonRecordField(RecordField recordFieldToDecorate) {
        super(recordFieldToDecorate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecordFieldType getType() {
        return new PythonRecordFieldType(super.getType());
    }

    /**
     * Returns the field name formatted into lowercase with underscores (Python
     * naming convention).
     * 
     * @return the field name formatted into lowercase with underscores
     */
    public String getSnakeCaseName() {
        return PythonElementUtils.getSnakeCaseValue(getName());
    }

    @Override
    protected String getQuotationString() {
        return "'";
    }

    @Override
    protected String getNullString() {
        return "None";
    }

}
