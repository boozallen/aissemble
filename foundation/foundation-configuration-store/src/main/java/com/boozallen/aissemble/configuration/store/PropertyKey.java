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
