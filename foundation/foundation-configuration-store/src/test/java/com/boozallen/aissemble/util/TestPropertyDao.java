package com.boozallen.aissemble.util;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Configuration::Store
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.configuration.dao.InMemoryPropertyDao;
import com.boozallen.aissemble.configuration.store.Property;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class TestPropertyDao extends InMemoryPropertyDao {
    public static final Map<String, Property> loadedProperties = new HashMap<>();

    @Override
    public void write(Set<Property> properties) {
        super.write(properties);
        // also save locally for testing purpose
        for (Property property : new ArrayList<>(properties)) {
            loadedProperties.put(property.getGroupName() + "-" + property.getPropertyName(), property);
        }
    }
}
