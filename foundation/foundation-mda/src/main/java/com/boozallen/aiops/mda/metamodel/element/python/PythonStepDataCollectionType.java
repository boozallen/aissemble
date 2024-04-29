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

import com.boozallen.aiops.mda.metamodel.element.BaseStepDataCollectionTypeDecorator;
import com.boozallen.aiops.mda.metamodel.element.DictionaryType;
import com.boozallen.aiops.mda.metamodel.element.StepDataCollectionType;

/**
 * Decorates StepDataCollectionType with Python-specific functionality.
 */
public class PythonStepDataCollectionType extends BaseStepDataCollectionTypeDecorator {

    /**
     * {@inheritDoc}
     */
    public PythonStepDataCollectionType(StepDataCollectionType stepDataCollectionTypeToDecorate) {
        super(stepDataCollectionTypeToDecorate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DictionaryType getDictionaryType() {
        return new PythonDictionaryType(super.getDictionaryType());
    }

    /**
     * Returns the short type of this step data collection type.
     * 
     * @return short type
     */
    public String getShortType() {
        PythonDictionaryType dictionaryType = (PythonDictionaryType) getDictionaryType();
        return dictionaryType.getShortType();
    }

    /**
     * Returns the fully qualified type of this step data collection type.
     * 
     * @return fully qualified type
     */
    public String getFullyQualifiedType() {
        PythonDictionaryType dictionaryType = (PythonDictionaryType) getDictionaryType();
        return dictionaryType.getFullyQualifiedType();
    }

}
