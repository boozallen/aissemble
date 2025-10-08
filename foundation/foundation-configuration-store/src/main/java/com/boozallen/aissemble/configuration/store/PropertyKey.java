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
 * Represents a key for property in the configuration store consisting of a {@link String} group name and {@link String} property name.
 */
public class PropertyKey {
    private String groupName;
    private String propertyName;

    public PropertyKey(String groupName, String propertyName) {
        setGroupName(groupName);
        setPropertyName(propertyName);
    }

    public String getGroupName() {
        return this.groupName;
    }
    private void setGroupName(String groupName) {
        this.groupName = Objects.requireNonNull(groupName, "Property groupName cannot be null");
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = Objects.requireNonNull(propertyName, "Property propertyName cannot be null");
    }

    @Override
    public String toString() {
        return String.format("PropertyKey{propertyName='%s', groupName='%s'}", this.propertyName, this.groupName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PropertyKey)) {
            return false;
        }

        PropertyKey propertyKey = (PropertyKey) o;
        return Objects.equals(this.groupName, propertyKey.getGroupName()) &&
                Objects.equals(this.propertyName, propertyKey.getPropertyName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.groupName, this.propertyName);
    }
}
