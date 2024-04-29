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
 * Defines the contract for the collection type of the data being passed into,
 * out of, or persisted in a pipeline step.
 */
public interface StepDataCollectionType extends NamespacedMetamodel {

    /**
     * Returns the dictionary entry for this type.
     * 
     * @return dictionary entry
     */
    DictionaryType getDictionaryType();

}
