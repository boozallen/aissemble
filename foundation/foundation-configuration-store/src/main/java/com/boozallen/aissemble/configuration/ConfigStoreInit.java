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

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import com.boozallen.aissemble.configuration.dao.ConfigStoreDaoClass;
import com.boozallen.aissemble.configuration.store.ConfigLoader;
import com.boozallen.aissemble.configuration.store.Property;
import com.boozallen.aissemble.configuration.policy.PropertyRegenerationPolicy;

import io.quarkus.runtime.Startup;

import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.spi.CDI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

/**
 * ConfigStoreInit loads configuration properties as application starts up
 */
@Startup
@ApplicationScoped
public class ConfigStoreInit {
    private static final Logger logger = LoggerFactory.getLogger(ConfigStoreInit.class);
    private static Status status;
    private static final String KRAUSENING_BASE = "KRAUSENING_BASE";
    private static final String DEFAULT_KRAUSENING_BASE_PATH = "/krausening/base";
    private static final String KRAUSENING_EXTENSIONS = "KRAUSENING_EXTENSIONS";
    private static final String DEFAULT_KRAUSENING_EXTENSIONS_PATH = "/krausening/extensions";
    private static final String KRAUSENING_PATH_PREFIX = "KRAUSENING_PATH_PREFIX";

    @PostConstruct
    public void init() {
        logger.info("Initialize store configuration properties and policies...");

        // Set Krausening env vars to a local path
        setKrauseningPaths();

        // Get users desired storage class. If not set then default to Krausening
        String storageClass = getBootstrapConfiguration("STORAGE_CLASS") != null ?
                getBootstrapConfiguration("STORAGE_CLASS") : ConfigStoreDaoClass.krausening.name();
        String propertyDaoClass = EnumUtils.isValidEnum(ConfigStoreDaoClass.class, storageClass)?
                ConfigStoreDaoClass.valueOf(storageClass).getValue() : storageClass;

        logger.info("Using property Dao class: {}", propertyDaoClass);

        try {
            // Pull the original configs onto the device
            // Because the configs are read only, these copied files are loaded into the config-store and manipulated
            // if needed.
            pullConfigs();

            ConfigLoader configLoader = CDI.current().select(ConfigLoader.class, new Any.Literal()).get();
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

    private void pullConfigs() throws IOException {
        String krauseningPath = getBootstrapConfiguration(KRAUSENING_BASE);
        String basePropertyUri = getBootstrapConfiguration("BASE_PROPERTY");
        if (StringUtils.isEmpty(basePropertyUri)) {
            throw new RuntimeException("Base property location not provided. The BASE_PROPERTY env variable must be set " +
                    "to the URI of the base properties files.");
        }
        copyPropertyFilesToLocal(krauseningPath, basePropertyUri);

        krauseningPath = getBootstrapConfiguration(KRAUSENING_EXTENSIONS);
        String environmentPropertyUri = getBootstrapConfiguration("ENVIRONMENT_PROPERTY");
        if (StringUtils.isEmpty(environmentPropertyUri)) {
            logger.warn("Environment property location not provided. Set the ENVIRONMENT_PROPERTY env " +
                    "variable to the URI of the env properties files to provide environment-specific overrides.");
        }
        copyPropertyFilesToLocal(krauseningPath, environmentPropertyUri);
    }

    private void copyPropertyFilesToLocal(String localPath, String remotePath) throws IOException {
        Files.createDirectories(Paths.get(localPath));
        if (StringUtils.isNotEmpty(remotePath)) {
            Path basePropertyPath = Paths.get(remotePath);
            try (Stream<Path> stream = Files.walk(basePropertyPath)) {
                stream.forEach(source -> copy(source, Paths.get(localPath)
                        .resolve(basePropertyPath.relativize(source))));
            } catch (IOException e) {
                throw new RuntimeException("Failed to copy property files from remote uri: " + remotePath, e);
            }
        }
    }

    private void copy(Path source, Path dest) {
        try {
            if(!Files.isDirectory(dest)) {
                Files.copy(source, dest, REPLACE_EXISTING);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void loadConfigs(ConfigLoader configLoader) {
        String basePropertyUri = getBootstrapConfiguration("KRAUSENING_BASE");
        String environmentPropertyUri = getBootstrapConfiguration("KRAUSENING_EXTENSIONS");

        // Load and validate the properties
        Set<Property> properties;
        if (StringUtils.isNotBlank(basePropertyUri)) {
            if (StringUtils.isNotBlank(environmentPropertyUri)) {
                logger.debug("Loading configs for base and environment properties");
                properties = configLoader.loadConfigs(basePropertyUri, environmentPropertyUri);
            }
            else {
                logger.debug("Loading configs for base properties only");
                properties = configLoader.loadConfigs(basePropertyUri);
            }
        }
        else {
            throw new RuntimeException("Failed to retrieve configurations properly");
        }
        configLoader.write(properties);
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

    private void setKrauseningPaths() {
        String prefix;
        if(StringUtils.isNotEmpty(getBootstrapConfiguration(KRAUSENING_PATH_PREFIX))) {
            prefix = getBootstrapConfiguration(KRAUSENING_PATH_PREFIX);
        } else {
            prefix = getBootstrapConfiguration("user.home");
        }
        System.setProperty(KRAUSENING_BASE, prefix + DEFAULT_KRAUSENING_BASE_PATH);
        System.setProperty(KRAUSENING_EXTENSIONS, prefix + DEFAULT_KRAUSENING_EXTENSIONS_PATH);
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

