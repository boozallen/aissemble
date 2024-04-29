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

import org.technologybrewery.fermenter.mda.metamodel.element.Validatable;

/**
 * Defines the contract for how a step will persist.
 */
public interface Persist extends Validatable {

    /**
     * Returns the persistence type of this step.
     * 
     * @return persistence type
     */
    String getType();

    /**
     * Returns the persistence mode of this step.
     * 
     * @return persistence mode
     */
    String getMode();

    /**
     * This value allows for customization of the collection type (e.g., dataset). The value will be looked up in
     * type.json and translated.
     * 
     * @return collection type
     */
    StepDataCollectionType getCollectionType();

    /**
     * The record type that is being passed. The record will be looked up in the metamodel repository and translated.
     * 
     * @return record type
     */
    StepDataRecordType getRecordType();

}
