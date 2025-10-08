package com.boozallen.aissemble.util;

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

import com.boozallen.aissemble.configuration.dao.InMemoryPropertyDao;
import com.boozallen.aissemble.configuration.store.Property;

import jakarta.enterprise.context.ApplicationScoped;
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
