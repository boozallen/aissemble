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

import com.boozallen.aissemble.configuration.store.Property;
import com.boozallen.aissemble.configuration.store.PropertyKey;

import java.util.Set;

/**
 * PropertyDao reads/writes the configuration property to the store
 */

public interface PropertyDao extends Dao<Property,PropertyKey> {
    /**
     * Read property from store with given {@link PropertyKey} containing the group name and property name
     * @param PropertyKey property key
     * @return Property
     */
    Property read(PropertyKey propertyKey);

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
