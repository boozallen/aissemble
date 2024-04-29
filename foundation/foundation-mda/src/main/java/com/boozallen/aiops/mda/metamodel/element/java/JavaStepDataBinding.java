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

import com.boozallen.aiops.mda.metamodel.element.BaseStepDataBindingDecorator;
import com.boozallen.aiops.mda.metamodel.element.StepDataBinding;
import com.boozallen.aiops.mda.metamodel.element.StepDataRecordType;

public class JavaStepDataBinding extends BaseStepDataBindingDecorator {
    /**
     * New decorator for {@link StepDataBinding}.
     *
     * @param stepDataBindingToDecorate instance to decorate
     */
    public JavaStepDataBinding(StepDataBinding stepDataBindingToDecorate) {
        super(stepDataBindingToDecorate);
    }

    @Override
    public StepDataRecordType getRecordType() {
        return super.getRecordType() != null ? new JavaStepDataRecordType(super.getRecordType()) : null;
    }
}
