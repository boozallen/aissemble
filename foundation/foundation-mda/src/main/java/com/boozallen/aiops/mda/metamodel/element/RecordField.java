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

/**
 * Defines the contract for an entry in the dictionary (e.g., validation, policies).
 */
public interface RecordField extends AbstractField {

    /**
     * Returns the type of the field.
     * 
     * @return type
     */
    RecordFieldType getType();

    /**
     * Returns the description of the field.
     * 
     * @return field description
     */
    String getDescription();

}
