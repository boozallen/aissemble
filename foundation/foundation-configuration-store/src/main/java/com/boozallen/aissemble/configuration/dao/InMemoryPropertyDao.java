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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
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
    public Property read(String groupName, String propertyName) {
        logger.info(String.format("Read property request received, groupName: %s, propertyName: %s:", groupName, propertyName));
        return this.store.get(groupName + "-" + propertyName);
    }

    @Override
    public void write(Property property) {
        this.store.put(property.getGroupName() + "-" + property.getName(), property);
        logger.info("Write property request received");
    }

    @Override
    public void write(Set<Property> properties) {
        for (Property property : new ArrayList<>(properties)) {
            this.store.put(property.getGroupName() + "-" + property.getName(), property);
        }
        logger.info("Write properties request received");
    }
}
