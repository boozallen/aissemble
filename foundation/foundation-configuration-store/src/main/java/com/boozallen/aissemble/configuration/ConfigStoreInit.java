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
import com.boozallen.aissemble.configuration.policy.PropertyRegenerationPolicy;

import io.quarkus.runtime.Startup;
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
        // We are loading all the properties upfront. In the future it might be more advantageous to load them as needed
        String basePropertyUri = System.getProperty("KRAUSENING_BASE");
        String environmentPropertyUri = System.getProperty("KRAUSENING_EXTENSIONS");
        String basePolicyUri = System.getenv("BASE_POLICY_URI");
        String environmentPolicyUri = System.getenv("ENVIRONMENT_POLICY_URI");

        try {
            ConfigLoader configLoader = CDI.current().select(ConfigLoader.class,new Any.Literal()).get();
            if (configLoader.isFullyLoaded()) {
                logger.info("Properties are already fully loaded. Skipping reload.");
                status = Status.LOAD_SKIPPED;
                return;
            }

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

            // Load and validate the policies
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

            configLoader.write(properties);
            // TODO configLoader.write(policies);

            logger.info("Successfully initialized store configuration properties and policies.");
            status = Status.LOAD_COMPLETE;

        } catch (Exception e) {
            logger.warn("Error loading properties:", e);
        }
    }

    public static String getStatus() {
        return ConfigStoreInit.status.getValue();
    }
}

enum Status {
    LOAD_COMPLETE("Load Complete"),
    LOAD_SKIPPED("Load Skipped");

    private String value;

    Status(final String value){
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }
}
