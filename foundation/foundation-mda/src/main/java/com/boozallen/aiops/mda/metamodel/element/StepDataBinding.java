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
 * Defines the contract for passing data into or out of a pipeline step.
 */
public interface StepDataBinding extends Validatable {

    /**
     * Returns the type of this data binding (e.g., message, native).
     * 
     * @return type of data binding
     */
    String getType();

    /**
     * If type is native, this value allows for customization of the collection type (e.g., dataset). The value will be
     * looked up in type.json and translated.
     * 
     * @return native collection type
     */
    StepDataCollectionType getNativeCollectionType();
    
    /**
     * The record type that is being passed. The record will be looked up in the metamodel repository and translated.
     * 
     * @return record type
     */
    StepDataRecordType getRecordType();    

    /**
     * Returns the type of an external location where data will be stored (e.g., topic, queue, directory).
     * 
     * @return name of an external location
     */
    String getChannelType();

    /**
     * Returns the name of an external location where data will be stored (e.g., topic name, queue name, directory
     * name).
     * 
     * @return name of an external location
     */
    String getChannelName();

}
