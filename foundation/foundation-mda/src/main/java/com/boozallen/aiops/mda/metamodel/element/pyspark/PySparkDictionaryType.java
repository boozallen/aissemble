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
import com.boozallen.aiops.mda.metamodel.element.python.PythonDictionaryType;

/**
 * Decorates DictionaryType with PySpark-specific functionality.
 */
public class PySparkDictionaryType extends PythonDictionaryType {

    /**
     * {@inheritDoc}
     */
    public PySparkDictionaryType(DictionaryType dictionaryTypeToDecorate) {
        super(dictionaryTypeToDecorate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSimpleType() {
        return wrapped.getSimpleType() + "-pyspark";
    }

}
