package com.boozallen.aissemble.configuration.dao;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Configuration::Store
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */


import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.krausening.Krausening;

import com.boozallen.aissemble.configuration.store.Property;
import com.boozallen.aissemble.configuration.store.PropertyKey;

/**
 * KrauseningPropertyDao reads and writes configuration property using Krausening file watching
 */
@ApplicationScoped
public class KrauseningPropertyDao implements PropertyDao {
    private static final Logger logger = LoggerFactory.getLogger(KrauseningPropertyDao.class);
    private static final String PROPERTIES_FILE_EXTENSION="properties";

    @Override
    public boolean checkEmpty() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public Property read(PropertyKey propertyKey) {
        logger.info(String.format("Read property request received, groupName: %s, propertyName: %s",
                propertyKey.getGroupName(), propertyKey.getPropertyName()));
        String fileName = String.format("%s.%s", propertyKey.getGroupName(), PROPERTIES_FILE_EXTENSION);
        Krausening krausening = Krausening.getInstance();
        Properties krauseningProperties = krausening.getProperties(fileName);

        // If the config is not found then return null
        if(krauseningProperties == null || krauseningProperties.get(propertyKey.getPropertyName()) == null){
            return null;
        }
        return new Property(propertyKey, krauseningProperties.get(propertyKey.getPropertyName()).toString());
    }

    @Override
    public void write(Property property) {
        try {
            logger.info(String.format("Write property request received, groupName: %s, propertyName: %s",
                    property.getPropertyKey().getGroupName(), property.getPropertyKey().getPropertyName()));
            String fileName = String.format("%s.%s", property.getPropertyKey().getGroupName(),
                    PROPERTIES_FILE_EXTENSION);
            Krausening krausening = Krausening.getInstance();
            Properties properties = krausening.getProperties(fileName) != null? krausening.getProperties(fileName):
                    new Properties();
            properties.setProperty(property.getPropertyKey().getPropertyName(), property.getValue());
            properties.store(new FileOutputStream(String.format("%s/%s", System.getProperty("KRAUSENING_BASE"),
                    fileName)), null);
        }catch (IOException e){
            logger.error("Error updating properties.", e);
        }
    }

    @Override
    public void write(Set<Property> properties) {
        logger.info("Write properties request received");
        for (Property property: properties) {
            this.write(property);
        }
    }

    @Override
    public boolean requiresInitialConfigLoad() {
        return false;
    }
}
