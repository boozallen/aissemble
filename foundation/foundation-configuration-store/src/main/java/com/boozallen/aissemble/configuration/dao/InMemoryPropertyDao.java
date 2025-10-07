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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * InMemoryPropertyDao
 * This is not a production ready class, which serves as a simple save to memory property dao class
 * ,and it can be used to check whether the request is received
 */

@ApplicationScoped
public class InMemoryPropertyDao implements PropertyDao {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryPropertyDao.class);
    private final Map<String, Property> store = new HashMap<>();

    @Override
    public boolean checkEmpty() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public Property read(PropertyKey propertyKey) {
        logger.info(String.format("Read property request received, groupName: %s, propertyName: %s", propertyKey.getGroupName(), propertyKey.getPropertyName()));
        return this.store.get(propertyKey.getGroupName() + "-" + propertyKey.getPropertyName());
    }

    @Override
    public void write(Property property) {
        this.store.put(property.getGroupName() + "-" + property.getPropertyName(), property);
        logger.info("Write property request received");
    }

    @Override
    public void write(Set<Property> properties) {
        for (Property property : new ArrayList<>(properties)) {
            this.store.put(property.getGroupName() + "-" + property.getPropertyName(), property);
        }
        logger.info("Write properties request received");
    }
}
