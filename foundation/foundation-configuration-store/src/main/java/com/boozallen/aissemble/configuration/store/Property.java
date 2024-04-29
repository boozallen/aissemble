package com.boozallen.aissemble.configuration.store;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Configuration::Store
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.Objects;

public class Property {
    private String groupName;
    private String name;
    private String value;
    public Property(String groupName, String name, String value) {
        setGroupName(groupName);
        setName(name);
        setValue(value);
    }

    public String getGroupName() {
        return groupName;
    }
    private void setGroupName(String groupName) {
        this.groupName = Objects.requireNonNull(groupName, "Property groupName cannot be null");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "Property name cannot be null");;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = Objects.requireNonNull(value, "Property value cannot be null");
    }

    @Override
    public String toString() {
        return "Property{" +
                "name='" + name +
                ", value='" + value +
                ", groupName='" + groupName +
                '}';
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
        return Objects.equals(groupName, property.getGroupName()) &&
                Objects.equals(name, property.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupName, name);
    }
}
