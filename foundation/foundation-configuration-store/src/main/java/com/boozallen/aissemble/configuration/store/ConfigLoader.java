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


import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.InvalidPathException;

import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.boozallen.aissemble.configuration.dao.PropertyDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * Handles parsing/reconciling configurations and associated metadata.
 */
public class ConfigLoader {

    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);
    private final PropertyDao propertyDao;
    public ConfigLoader(PropertyDao propertyDao) {
        this.propertyDao = propertyDao;
    }

    /**
     * Loads configurations from the base and environment URIs and reconciles them.
     * @param baseURI URI housing the base/default configuration files.
     * @param environmentURI URI housing overrides of agumentations specific to the environment.
     * @return Set of properties.
     */
    public Set<Property> loadConfigs(String baseURI, String environmentURI) {
        Set<Property> baseConfigs = loadURI(baseURI);
        Set<Property> environmentConfigs = loadURI(environmentURI);

        return reconcileConfigs(baseConfigs, environmentConfigs);
    }

    /**
     * Loads configuration from the provided uri
     * @param baseURI URI housing the configuration files
     * @return Set of properties.
     */
    public Set<Property> loadConfigs(String baseURI) {
        return loadURI(baseURI);
    }

    /**
     * Inspects config file(s) at the given URI and gathers all properties.
     * @param URI Location housing configuration yamls.
     * @return Set of Property objects.
     */
    private Set<Property> loadURI(String URI) {
        if (URI == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }

        List<File> yamlFiles = getYamlFiles(URI);
        Set<Property> aggregateProperties = new HashSet<>();
        for (File yamlFile : yamlFiles) {
            logger.info("Loading: " + yamlFile.getName());
            for (Property property : parseYaml(yamlFile.getAbsolutePath())) {
                // if the property already existed in the set, then add call returns false
                if (!aggregateProperties.add(property)) {
                    throw new IllegalArgumentException("Duplicates found");
                }
            }
        }

        return aggregateProperties;
    }

    /**
     * Filters and collects yaml files at the URI.
     * @param URI Location housing configuration yamls.
     * @return List of yaml files.
     */
    private List<File> getYamlFiles(String URI) {
        try (Stream<Path> walk = Files.walk(Paths.get(URI))) {
            return walk
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".yaml") || path.toString().endsWith(".yml"))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        } catch (IOException | InvalidPathException e) {
            throw new RuntimeException("Error accessing configuration files at " + URI, e);
        }
    }

    /**
     * Deserializes the yaml file's contents into java objects.
     * @param filePath Path to the yaml file.
     * @return Set of Property objects.
     */
    private Set<Property> parseYaml(String filePath) {
        try {
            LoaderOptions loaderOptions = new LoaderOptions();
            loaderOptions.setAllowDuplicateKeys(false);
            Constructor constructor = new Constructor(YamlConfig.class, loaderOptions);
            TypeDescription configDescription = new TypeDescription(YamlConfig.class);
            configDescription.addPropertyParameters("properties", YamlProperty.class);
            constructor.addTypeDescription(configDescription);
            Yaml yaml = new Yaml(constructor);

            FileInputStream inputStream = new FileInputStream(filePath);
            YamlConfig yamlConfig = yaml.load(inputStream);
            return yamlConfig.toPropertySet();
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not parse yaml", e);
        }
    }

    /**
     * Overrides and augments base configs with environment configs.
     * @param baseConfigs Set of properties defined at the base URI.
     * @param environmentConfigs Set of properties defined at the environment URI.
     * @return Set of properties.
     */
    private Set<Property> reconcileConfigs(Set<Property> baseConfigs, Set<Property> environmentConfigs) {
        environmentConfigs.addAll(baseConfigs);
        return environmentConfigs;
    }

    /**
     * Write give properties set to the store
     * @param properties to be written to store
     */
    public void write(Set<Property> properties) {
        propertyDao.write(properties);
        logger.info(String.format("Successfully write properties to the store: %s", properties));
    }

    /**
     * Read the Property from store with given group name and property name
     * @param groupName group name
     * @param propertyName property name
     * @return property read from the store
     */
    public Property read(String groupName, String propertyName) {
        logger.info(String.format("Read property with groupName: %s, propertyName: %s from the store.", groupName, propertyName));
        return propertyDao.read(groupName, propertyName);
    }
}


