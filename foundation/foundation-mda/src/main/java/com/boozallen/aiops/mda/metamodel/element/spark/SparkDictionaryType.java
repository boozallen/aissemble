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
import com.boozallen.aiops.mda.metamodel.element.java.JavaDictionaryType;
import org.technologybrewery.fermenter.mda.TypeManager;

/**
 * Decorates DictionaryType with Spark-specific functionality.
 */
public class SparkDictionaryType extends JavaDictionaryType {

    /**
     * {@inheritDoc}
     */
    public SparkDictionaryType(DictionaryType dictionaryTypeToDecorate) {
        super(dictionaryTypeToDecorate);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getSimpleType() {
        return super.getSimpleType() + "-spark";
    }

    public String getGenericShortType() {
        return TypeManager.getShortType(super.getSimpleType());
    }

    public String getGenericType() {
        return super.getSimpleType();
    }

}
