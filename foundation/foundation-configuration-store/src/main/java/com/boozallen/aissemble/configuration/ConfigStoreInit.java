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

import com.boozallen.aissemble.configuration.store.ConfigLoader;
import com.boozallen.aissemble.configuration.store.Property;
import io.quarkus.runtime.Startup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.CDI;
import java.util.HashSet;
import java.util.Set;

/**
 * ConfigStoreInit loads configuration properties as application starts up
 */
@Startup
@ApplicationScoped
public class ConfigStoreInit {
    private static final Logger logger = LoggerFactory.getLogger(ConfigStoreInit.class);

    @PostConstruct
    protected void init() {
        logger.info("Initialize store configuration properties...");
        String baseUri = System.getenv("BASE_URI");
        String environmentUri = System.getenv("ENVIRONMENT_URI");
        try {
            ConfigLoader configLoader = CDI.current().select(ConfigLoader.class,new Any.Literal()).get();
            if (configLoader.isFullyLoaded()) {
                logger.info("Properties are already fully loaded. Skipping reload.");
                return;
            }
            Set<Property> properties;
            if (environmentUri != null && !environmentUri.isEmpty()) {
                properties = configLoader.loadConfigs(baseUri,environmentUri);
            } else if (baseUri != null && !baseUri.isEmpty()){
                properties = configLoader.loadConfigs(baseUri);
            } else {
                throw new RuntimeException("Undefined environment variables: BASE_URI and ENVIRONMENT_URI");
            }
            configLoader.write(properties);
        } catch (Exception e) {
            throw new RuntimeException("Error loading properties", e);
        }
    }
}
