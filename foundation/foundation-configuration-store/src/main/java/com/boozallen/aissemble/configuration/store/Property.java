package com.boozallen.aissemble.configuration.store;

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

import java.util.Objects;

/**
 * Represents a property in the configuration store consisting of a {@link PropertyKey} key and {@link String} value.
 */
public class Property {
    private PropertyKey propertyKey;
    private String value;

    public Property(PropertyKey propertyKey, String value) {
        setPropertyKey(propertyKey);
        setValue(value);
    }

    public Property(String groupName, String propertyName, String value) {
        this(new PropertyKey(groupName, propertyName), value);
    }

    public PropertyKey getPropertyKey() {
        return this.propertyKey;
    }

    public void setPropertyKey(PropertyKey propertyKey) {
        this.propertyKey = propertyKey;
    }

    public String getGroupName() {
        return this.propertyKey.getGroupName();
    }

    public String getPropertyName() {
        return this.propertyKey.getPropertyName();
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = Objects.requireNonNull(value, "Property value cannot be null");
    }

    @Override
    public String toString() {
        // We won't print out the value in case of sensitive info
        return String.format("Property{%s}", this.propertyKey.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Property)) {
            return false;
        }

        // does not include value to enable check of erroneously indistinguisabe properties,
        // where two or more properties have identical groupName and name (but different values)
        Property property = (Property) o;
        return Objects.equals(this.propertyKey, property.getPropertyKey());
    }

    @Override
    public int hashCode() {
        return this.propertyKey.hashCode();
    }

    public String toJsonString() {
        return String.format("{\"groupName\":\"%s\",\"name\":\"%s\",\"value\":\"%s\"}", getGroupName(), getPropertyName(), this.value);
    }
}
