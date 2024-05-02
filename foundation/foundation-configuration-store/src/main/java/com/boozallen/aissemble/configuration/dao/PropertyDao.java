package com.boozallen.aissemble.configuration.dao;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Configuration::Store
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.configuration.exception.PropertyDaoException;
import com.boozallen.aissemble.configuration.store.Property;

import java.util.Set;

/**
 * PropertyDao reads/writes the configuration property to the store
 */
public interface PropertyDao extends Dao<Property>{
    /**
     * Read property from store with given group name and property name
     * @param groupName group name
     * @param propertyName property name
     * @return Property
     */
    Property read(String groupName, String propertyName);

    /**
     * Write given property to the store
     * @param property to be written to store
     */
    void write(Property property);


    /**
     * Write given Set of property to the store
     * @param properties to be written to store
     */
    void write(Set<Property> properties);
}
