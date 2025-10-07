package com.boozallen.aiops.metadata.hive.config;

/*-
 * #%L
 * AIOps Docker Baseline::AIOps Metadata Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
