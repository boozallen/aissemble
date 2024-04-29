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
 * Houses the deserialized contents of an object under "properties:" of a configuration yaml.
 */
public class YamlProperty {
    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof YamlProperty)) {
            return false;
        }

        // does not include value to enable check of erroneously indistinguisabe properties,
        // where two or more properties have identical groupName and name (but different values)
        YamlProperty yamlProperty = (YamlProperty) o;
        return Objects.equals(name, yamlProperty.getName()) &&
                Objects.equals(value, yamlProperty.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

}
