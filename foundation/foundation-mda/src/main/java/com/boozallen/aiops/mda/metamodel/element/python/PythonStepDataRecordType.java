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

import com.boozallen.aiops.mda.metamodel.element.BaseStepDataRecordTypeDecorator;
import com.boozallen.aiops.mda.metamodel.element.StepDataRecordType;
import com.boozallen.aiops.mda.metamodel.element.util.PythonElementUtils;

/**
 * Decorates StepDataRecordType with Python-specific functionality.
 */
public class PythonStepDataRecordType extends BaseStepDataRecordTypeDecorator {

    /**
     * {@inheritDoc}
     */
    public PythonStepDataRecordType(StepDataRecordType stepDataRecordTypeToDecorate) {
        super(stepDataRecordTypeToDecorate);
    }

    /**
     * Returns the fully qualified type of this step data record type.
     * 
     * @return fully qualified type
     */
    public String getFullyQualifiedType() {
        // python records are generated under src/${packageName}/record/
        // so their fully qualified implementation is:
        // record.record_name.RecordName
        String recordName = getName();
        String packageName = "record." + PythonElementUtils.getSnakeCaseValue(recordName);

        return packageName + "." + recordName;
    }

}
