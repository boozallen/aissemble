package com.boozallen.aiops.mda.metamodel.element;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.technologybrewery.fermenter.mda.metamodel.element.NamespacedMetamodel;

/**
 * Defines the contract for field types on a record (dictionary).
 */
public interface RecordFieldType extends NamespacedMetamodel {

    /**
     * Returns true if the field is sourced from a dictionary.
     * 
     * @return whether or not a dictionary type
     */
    boolean isDictionaryTyped();

    /**
     * Returns the dictionary entry for this type, only if it is a dictionary type.
     * 
     * @return dictionary entry
     */
    DictionaryType getDictionaryType();    

}
