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

import com.boozallen.aiops.mda.metamodel.AIOpsModelInstanceRepostory;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceRepositoryManager;

import com.boozallen.aiops.mda.metamodel.element.BaseStepDataRecordTypeDecorator;
import com.boozallen.aiops.mda.metamodel.element.StepDataRecordType;

/**
 * Decorates StepDataRecordType with Java-specific functionality.
 */
public class JavaStepDataRecordType extends BaseStepDataRecordTypeDecorator {

    private AIOpsModelInstanceRepostory modelRepository = ModelInstanceRepositoryManager
            .getMetamodelRepository(AIOpsModelInstanceRepostory.class);

    /**
     * {@inheritDoc}
     */
    public JavaStepDataRecordType(StepDataRecordType stepDataRecordTypeToDecorate) {
        super(stepDataRecordTypeToDecorate);
    }

    /**
     * Returns the fully qualified type of this step data record type.
     * 
     * @return fully qualified type
     */
    public String getFullyQualifiedType() {
        String fullyQualifiedRecordType = null;

        String packageName = getPackage();
        if (StringUtils.isNotBlank(packageName) && !packageName.equals(modelRepository.getBasePackage())) {
            fullyQualifiedRecordType = packageName + "." + getName();
        }

        return fullyQualifiedRecordType;
    }
}
