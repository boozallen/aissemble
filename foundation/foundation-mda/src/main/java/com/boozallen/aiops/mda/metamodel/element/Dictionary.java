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

import java.util.List;

import org.technologybrewery.fermenter.mda.metamodel.element.NamespacedMetamodel;

/**
 * Defines the contract for a dictionary that supports defining semantically rich types.
 */
public interface Dictionary extends NamespacedMetamodel {

    /**
     * Returns the types contained in this dictionary.
     * 
     * @return types
     */
    List<DictionaryType> getDictionaryTypes();

}
