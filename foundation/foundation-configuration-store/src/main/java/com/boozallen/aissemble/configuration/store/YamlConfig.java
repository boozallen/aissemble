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
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * Houses the deserialized contents of a configuration yaml.
 */
public class YamlConfig {
    private String groupName;
    private List<YamlProperty> properties; // must use List, as snakeyaml does not support sets

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<YamlProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<YamlProperty> properties) {
        this.properties = properties;
    }

    /**
     * Converts the YamlProperty objects into Property objects, and provides them in a set.
     * @return Set of Property objects.
     */
    public Set<Property> toPropertySet() {
        Set<Property> returnSet = new HashSet<>();
        for (YamlProperty yamlProperty : properties) {
            returnSet.add(new Property(groupName, yamlProperty.getName(), yamlProperty.getValue()));
        }
        return returnSet;
    }
}
