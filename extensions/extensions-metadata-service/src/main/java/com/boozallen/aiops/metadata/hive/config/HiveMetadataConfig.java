package com.boozallen.aiops.metadata.hive.config;

/*-
 * #%L
 * AIOps Docker Baseline::AIOps Metadata Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.aeonbits.owner.KrauseningConfig;

@KrauseningConfig.KrauseningSources("hive-metadata.properties")
public interface HiveMetadataConfig extends KrauseningConfig {

    /**
     * Hive table name to store metadata.
     * 
     * @return hive table name
     */
    @Key("table.name")
    @DefaultValue("metadata")
    String tableName();

    /**
     * Hive table format to store metadata.
     * 
     * NOTE: Default 'hive' format does not support multi-line values and will
     * create records for each new line of text in the metadata. Use a binary
     * storage format such as 'parquet' or 'avro' to support multi-line values.
     * 
     * @return hive table format
     */
    @Key("table.format")
    @DefaultValue("hive")
    String tableFormat();

    /**
     * Application name for the spark session
     *
     * @return spark session app name
     */
    @Key("spark.appname")
    @DefaultValue("hive-metadata-service")
    String sparkAppName();
}
