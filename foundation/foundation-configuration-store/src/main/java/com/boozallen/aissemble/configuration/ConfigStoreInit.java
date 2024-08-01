package com.boozallen.aissemble.configuration;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Configuration::Store
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.configuration.dao.ConfigStoreDaoClass;
import com.boozallen.aissemble.configuration.store.ConfigLoader;
import com.boozallen.aissemble.configuration.store.Property;
import com.boozallen.aissemble.configuration.policy.PropertyRegenerationPolicy;

import io.quarkus.runtime.Startup;

import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.CDI;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * ConfigStoreInit loads configuration properties as application starts up
 */
@Startup
@ApplicationScoped
public class ConfigStoreInit {
    private static final Logger logger = LoggerFactory.getLogger(ConfigStoreInit.class);
    private static Status status;

    @PostConstruct
    public void init() {
        logger.info("Initialize store configuration properties and policies...");

        // Get users desired storage class. If not set then default to Krausening
        String storageClass = getBootstrapConfiguration("STORAGE_CLASS") != null ?
                getBootstrapConfiguration("STORAGE_CLASS") : ConfigStoreDaoClass.krausening.name();
        String propertyDaoClass = EnumUtils.isValidEnum(ConfigStoreDaoClass.class, storageClass)?
                ConfigStoreDaoClass.valueOf(storageClass).getValue() : storageClass;

        logger.info("Using property Dao class: {}", propertyDaoClass);

        try {
            ConfigLoader configLoader = CDI.current().select(ConfigLoader.class,new Any.Literal()).get();
            configLoader.setPropertyDaoClass(propertyDaoClass);
            if (configLoader.isFullyLoaded()) {
                logger.info("Properties and policies are already fully loaded. Skipping reload.");
                status = Status.LOAD_SKIPPED;
                return;
            }

            loadConfigs(configLoader);
            loadPolicies(configLoader);

            logger.info("Successfully initialized store configuration properties and policies.");
            status = Status.LOAD_COMPLETE;
            configLoader.updateLoadStatus(true);

        } catch (Exception e) {
            logger.warn("Error loading properties:", e);
        }
    }

    private void loadConfigs(ConfigLoader configLoader) {
        if(configLoader.doInitialConfigLoad()) {
            String basePropertyUri = getBootstrapConfiguration("KRAUSENING_BASE");
            String environmentPropertyUri = getBootstrapConfiguration("KRAUSENING_EXTENSIONS");

            // Load and validate the properties
            Set<Property> properties;
            if (StringUtils.isNotBlank(basePropertyUri)) {
                if (StringUtils.isNotBlank(environmentPropertyUri)) {
                    logger.debug("Loading configs for base and environment properties");
                    properties = configLoader.loadConfigs(basePropertyUri,environmentPropertyUri);
                }
                else {
                    logger.debug("Loading configs for base properties only");
                    properties = configLoader.loadConfigs(basePropertyUri);
                }
            }
            else {
                throw new RuntimeException("Undefined environment variables: KRAUSENING_BASE and KRAUSENING_EXTENSIONS");
            }
            configLoader.write(properties);
        } else {
            logger.info("Skipping config loading on initialization");
        }
    }

    private void loadPolicies(ConfigLoader configLoader) {
        // Load and validate the policies
        String basePolicyUri = getBootstrapConfiguration("BASE_POLICY_URI");
        String environmentPolicyUri = getBootstrapConfiguration("ENVIRONMENT_POLICY_URI");
        Set<PropertyRegenerationPolicy> policies;
        if (StringUtils.isNotBlank(basePolicyUri)) {
            if (StringUtils.isNotBlank(environmentPolicyUri)) {
                logger.debug("Loading base and environment policies");
                policies = configLoader.loadPolicies(basePolicyUri,environmentPolicyUri);
            }
            else {
                logger.debug("Loading base policies only");
                policies = configLoader.loadPolicies(basePolicyUri);
            }
        }
        else {
            logger.info("No policies found to load");
        }
        // TODO configLoader.write(policies);
    }

    private static String getBootstrapConfiguration(String propertyName) {
        String propertyValue = System.getProperty(propertyName);
        if(propertyValue == null) {
            propertyValue = System.getenv(propertyName);
            if(propertyValue != null) {
                System.setProperty(propertyName, propertyValue);
            }
        }
        return propertyValue;
    }

    public static String getStatus() {
        return ConfigStoreInit.status.getValue();
    }

    private enum Status {
        LOAD_COMPLETE("Load Complete"),
        LOAD_SKIPPED("Load Skipped");

    private final String value;

        Status(final String value){
            this.value = value;
        }

        public String getValue(){
            return this.value;
        }
    }
}

