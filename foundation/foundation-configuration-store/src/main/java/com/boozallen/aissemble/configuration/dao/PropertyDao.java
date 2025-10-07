package com.boozallen.aissemble.configuration.dao;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Configuration::Store
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
     * @param propertyKey property key
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
