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
 * Defines the contract for a composite with one or more fields. A composite can contain fields, but not other
 * composites.
 */
public interface Composite extends NamespacedMetamodel {

    /**
     * Returns the description of this composite.
     * 
     * @return composite description
     */
    String getDescription();

    /**
     * Returns the fields contained in this field container.
     * 
     * @return fields
     */
    List<CompositeField> getFields();

}
