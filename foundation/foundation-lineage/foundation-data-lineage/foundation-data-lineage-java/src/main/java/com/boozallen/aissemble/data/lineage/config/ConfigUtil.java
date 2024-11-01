package com.boozallen.aissemble.data.lineage.config;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Data Lineage Java
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.technologybrewery.krausening.Krausening;

import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;

public class ConfigUtil {

    private static ConfigUtil instance = null;
    public static Krausening krausening;
    public static Properties properties;
    public static final String CONFIG_FILE = "data-lineage.properties";
    private static final String DEFAULT_PRODUCER = "http://github.com/producer";
    private static final String DEFAULT_DATA_LINEAGE_ENABLED = "true";
    private static final String DEFAULT_DATA_LINEAGE_SCHEMA_URL = "https://openlineage.io/spec/1-0-5/OpenLineage.json";
    private static final String DEFAULT_SHOULD_EMIT_TO_CONSOLE = "true";
    private static final String DEFAULT_SHOULD_EMIT_OVER_MESSAGING = "true";

    public static ConfigUtil getInstance() {
        if (instance == null) {
            instance = new ConfigUtil();
            krausening = Krausening.getInstance();
            krausening.loadProperties();
            properties = krausening.getProperties(CONFIG_FILE);
            if (properties == null) {
                properties = new Properties();
            }
        }
        return instance;
    }

    /**
     * Configures whether data lineage events will be sent as part of pipeline executions. Defaults to true, this
     * should probably be set to false for pipeline tests that do not have a consumer for data lineage events.
     * @return Whether data lineage is enabled
     */
    public String getDataLineageEnabled() {
        return properties.getProperty("data.lineage.enabled", DEFAULT_DATA_LINEAGE_ENABLED);
    }

    /**
     * Configures the value of the producer field of data lineage objects.
     * @param jobName
     * @return the default or job-specific producer field
     */
    public String getProducer(String jobName) {
        String jobProducerProperty = String.format("data.lineage.%s.producer", jobName);
        return properties.getProperty(jobProducerProperty, DEFAULT_PRODUCER);
    }

    public String getProducer() {
        return DEFAULT_PRODUCER;
    }

    /**
     * Configures the Job namespace to write events to in a downstream consumer.
     * @param jobName
     * @param defaultNamespace
     * @return The job-specific namespace, usually the overarching process.
     */
    public String getJobNamespace(String jobName, String defaultNamespace) {
        String jobNamespaceProperty = String.format("data.lineage.%s.namespace", jobName);
        Set<String> propertyNames = properties.stringPropertyNames();

        if (propertyNames.contains(jobNamespaceProperty)) {
            return properties.getProperty(jobNamespaceProperty);
        } else if (defaultNamespace != null) {
            return defaultNamespace;
        } else {
            throw new NoSuchElementException(getNoPropertyErrorMessage(jobNamespaceProperty));
        }
    }

    /**
     * Dataset namespace to write events to in a downstream consumer.
     * @param datasetName
     * @return The dataset-specific namespace, usually the data source.
     */
    public String getDatasetNamespace(String datasetName) {
        String datasetNamespaceProperty = String.format("data.lineage.%s.namespace", datasetName);
        Set<String> propertyNames = properties.stringPropertyNames();

        if (propertyNames.contains(datasetNamespaceProperty)) {
            return properties.getProperty(datasetNamespaceProperty);
        } else {
            throw new NoSuchElementException(getNoPropertyErrorMessage(datasetNamespaceProperty));
        }
    }

    /**
     * Configures the value of the schema url field of data lineage objects
     * @return The value of the schema url field
     */
    public String getDataLineageSchemaUrl() {
        return properties.getProperty("data.lineage.schema.url", DEFAULT_DATA_LINEAGE_SCHEMA_URL);
    }

    /**
     * Configures whether to enable supplemental emission of lineage events to the Console.
     * @return opt-in vs opt-out of supplemental console emission.
     */
    public String shouldEmitToConsole() {
        return properties.getProperty("data.lineage.emission.console", DEFAULT_SHOULD_EMIT_TO_CONSOLE);
    }

    /**
     * Configures whether to enable emission of lineage events via Messaging.
     * @return opt-in vs opt-out of messaging
     */
    public String shouldEmitOverMessaging() {
        return properties.getProperty("data.lineage.emission.messaging", DEFAULT_SHOULD_EMIT_OVER_MESSAGING);
    }

    private static String getNoPropertyErrorMessage(String propertyName) {
        return String.format("Could not find property %s.", propertyName);
    }
}
