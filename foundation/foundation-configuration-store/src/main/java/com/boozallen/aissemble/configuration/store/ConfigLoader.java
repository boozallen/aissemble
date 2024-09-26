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
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.krausening.Krausening;

import com.boozallen.aissemble.configuration.dao.PropertyDao;
import com.boozallen.aissemble.configuration.policy.PropertyRegenerationPolicy;
import com.boozallen.aissemble.configuration.policy.PropertyRegenerationPolicyManager;
import com.boozallen.aissemble.configuration.policy.exception.PropertyRegenerationPolicyException;
import com.boozallen.aissemble.core.policy.configuration.policymanager.AbstractPolicyManager;

/**
 * Handles parsing/reconciling configurations and associated metadata.
 */
@ApplicationScoped
public class ConfigLoader {

    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);
    private PropertyDao propertyDao;
    private Instance<PropertyDao> instances;

    @Inject
    public void setPropertyDaoInstances(Instance<PropertyDao> instances) {
        this.instances = instances;
    }

    public void setPropertyDaoClass(String propertyDaoClass) throws RuntimeException {
        this.instances.forEach(propertyDao -> {
            if (propertyDao.getClass().getName().contains(propertyDaoClass)) {
                this.propertyDao = propertyDao;
            }
        });
        if(this.propertyDao == null){
            throw new RuntimeException("Invalid Storage Class");
        }
    }

    protected PropertyDao getPropertyDao() {
        return this.propertyDao;
    }

    public void setPropertyDao(PropertyDao propertyDao) {
        this.propertyDao = propertyDao;
    }

    /**
     * Loads configurations from the base and environment URIs and reconciles them.
     * @param baseURI URI housing the base/default configuration files.
     * @param environmentURI URI housing overrides of augmentations specific to the environment.
     * @return Set of properties.
     */
    public Set<Property> loadConfigs(String baseURI, String environmentURI) {
        Set<Property> baseConfigs = loadPropertiesURI(baseURI);
        Set<Property> environmentConfigs = loadPropertiesURI(environmentURI);
        return reconcileConfigs(baseConfigs, environmentConfigs);
    }

    /**
     * Loads configuration from the provided uri
     * @param baseURI URI housing the configuration files
     * @return Set of properties.
     */
    public Set<Property> loadConfigs(String baseURI) {
        return loadPropertiesURI(baseURI);
    }

    /**
     * Inspects config file(s) at the given URI and gathers all properties.
     * @param uri Location housing configuration properties.
     * @return Set of Property objects.
     */
    private Set<Property> loadPropertiesURI(String uri) {
        if (uri == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }

        List<File> propertiesFiles = getPropertiesFiles(uri);
        Set<Property> aggregateProperties = new HashSet<>();
        for (File propertiesFile : propertiesFiles) {
            logger.info("Loading: {}", propertiesFile.getName());
            for (Property property : parseProperties(propertiesFile.getName())) {
                // if the property already existed in the set, then add call returns false
                if (!aggregateProperties.add(property)) {
                    throw new IllegalArgumentException("Duplicates found");
                }
            }
        }

        return aggregateProperties;
    }

    /**
     * Loads policies from the base and environment URIs and reconciles them.
     * @param baseURI URI housing the base/default policies
     * @param environmentURI URI housing environment specific policies.
     * @return Set of policies.
     */
    public Set<PropertyRegenerationPolicy> loadPolicies(String baseURI, String environmentURI) {
        Set<PropertyRegenerationPolicy> basePolicies = loadPolicyURI(baseURI);
        Set<PropertyRegenerationPolicy> environmentPolicies = loadPolicyURI(environmentURI);
        logger.info("Loaded {} base policies and {} environment policies", 
            basePolicies.size(), environmentPolicies.size());

        Set<PropertyRegenerationPolicy> reconciledPolicies = reconcilePolicies(basePolicies, environmentPolicies);
        logger.info("Using {} policies after reconciling environment against base", reconciledPolicies.size());

        return validatePolicies(reconciledPolicies);
    }

    /**
     * Loads policies from the provided uri
     * @param baseURI URI housing the base/default policies
     * @return Set of policies.
     */
    public Set<PropertyRegenerationPolicy> loadPolicies(String baseURI) {
        Set<PropertyRegenerationPolicy> policies = loadPolicyURI(baseURI);
        logger.info("Loaded {} policies", policies.size());

        return validatePolicies(policies);
    }

    /**
     * Gathers all the {@link PropertyRegenerationPolicy}'s at the given URI
     * @param URI Location housing policies.
     * @return Set of policies.
     */
    private Set<PropertyRegenerationPolicy> loadPolicyURI(String URI) {
        if (URI == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }
        
        // set the system property so the policy manager reads in from the desired URI
        System.setProperty(AbstractPolicyManager.getPolicyLocationPropertyKey(), URI);
        PropertyRegenerationPolicyManager policyManager = new PropertyRegenerationPolicyManager();
        
        return policyManager.getPropertyRegenerationPolicies(); 
    }

    /**
     * Override the base policies with their environment policy. 
     * @param basePolicies Map of policies defined at the base URI.
     * @param environmentPolicies Map of policies defined at the environment URI.
     * @return List of policies.
     */
    private Set<PropertyRegenerationPolicy> reconcilePolicies(Set<PropertyRegenerationPolicy> basePolicies, 
                                                                        Set<PropertyRegenerationPolicy> environmentPolicies) {
        environmentPolicies.addAll(basePolicies);
        return environmentPolicies;
    }

    /**
     * Validates there is at most one policy per target.
     * @param policies List of policies.
     * @return List of policies.
     */
    private Set<PropertyRegenerationPolicy> validatePolicies(Set<PropertyRegenerationPolicy> policies) {
        // if every targeted property key is only defined in one policy, then a list and set of all targeted property keys should be the same size
        List<PropertyKey> targetsList = new ArrayList<>();
        Set<PropertyKey> targetsSet = new HashSet<>();

        // iterate through each policy and its targeted property keys
        for (PropertyRegenerationPolicy policy: policies) {
            for (PropertyKey targetPropertyKey: policy.getTargetPropertyKeys()) {
                targetsList.add(targetPropertyKey);
                targetsSet.add(targetPropertyKey);
            }
        }

        if (targetsList.size() == targetsSet.size()) {
            logger.info("Policy validation complete");
            return policies;
        } else {
            throw new PropertyRegenerationPolicyException("Invalid Property Regeneration Policy configuration, found multiple policies with the same " + 
                                                            "property in the 'targets' field. There should be at most one policy per target property.");
        }
    }

    /**
     * Filters and collects properties files at the URI.
     * @param uri Location housing configuration properties.
     * @return List of properties files.
     */
    private List<File> getPropertiesFiles(String uri) {
        try (Stream<Path> walk = Files.walk(Paths.get(uri))) {
            return walk
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".properties"))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        } catch (IOException | InvalidPathException e) {
            throw new RuntimeException("Error accessing configuration files at " + uri, e);
        }
    }

    /**
     * Load the properties file's using Krausening and deserialize contents into java objects.
     * @param fileName name of the properties file.
     * @return Set of Property objects.
     */
    private Set<Property> parseProperties(String fileName) {
        try {
            Krausening krausening = Krausening.getInstance();
            Properties krauseningProperties = krausening.getProperties(fileName);
            Set<Property> properties = new HashSet<>();

            for (Object propertyName: krauseningProperties.keySet()){
                Property property = new Property(fileName.substring(0, fileName.indexOf('.')), propertyName.toString(),
                        krauseningProperties.getProperty(propertyName.toString()));
                properties.add(property);
            }

            return properties;
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not parse properties file", e);
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
        try {
            propertyDao.write(properties);
            logger.info("Successfully wrote all properties to the store.");
        } catch (Exception e) {
            logger.error("Error updating properties.", e);
            updateLoadStatus(false);
        }
    }

    /**
     * Read property from store with given {@link PropertyKey} containing the group name and property name
     * @param propertyKey property key
     * @return property read from the store
     */
    public Property read(PropertyKey propertyKey) {
        logger.info(String.format("Read property with groupName: %s, propertyName: %s from the store.", propertyKey.getGroupName(), propertyKey.getPropertyName()));
        return propertyDao.read(propertyKey);
    }
    
    public boolean isFullyLoaded() {
        try {
            Property statusProperty = propertyDao.read(new PropertyKey("load-status", "fully-loaded"));
            return statusProperty != null && BooleanUtils.toBoolean(statusProperty.getValue());
        } catch (Exception e) {
            logger.warn("Properties are not loaded previously, continue", e);
            return false;
        }
    }

    public void updateLoadStatus(boolean status) {
        Property statusProperty = new Property("load-status", "fully-loaded", String.valueOf(status));
        try {
            propertyDao.write(statusProperty);
            logger.info("Successfully updated load status to: " + status);
        } catch (Exception e) {
            logger.error("Error updating load status.", e);
        }
    }
}
