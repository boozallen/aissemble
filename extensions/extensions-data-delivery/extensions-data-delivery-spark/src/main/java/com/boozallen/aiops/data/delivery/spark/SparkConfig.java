package com.boozallen.aiops.data.delivery.spark;

/*-
 * #%L
 * AIOps Foundation::AIOps Data Delivery::Spark
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.aeonbits.owner.KrauseningConfig;

@KrauseningConfig.KrauseningSources("spark-data-delivery.properties")
public interface SparkConfig extends KrauseningConfig {

    /**
     * Batch size limit
     * 
     * @return the batch size limit
     */
    @Key("batch.size")
    @DefaultValue("50")
    Integer batchSize();

    /**
     * Batch interval in milliseconds
     * 
     * @return the batch interval in milliseconds
     */
    @Key("batch.interval.ms")
    @DefaultValue("30000")
    Integer batchIntervalMs();

    /**
     * Base location of the input data directory
     * 
     * @return the file path to the input data directory
     */
    @Key("input.directory")
    @DefaultValue("/opt/spark/data/")
    String inputDirectory();

    /**
     * Base location of the output directory
     * 
     * @return the file path to the output directory
     */
    @Key("output.directory")
    @DefaultValue("/tmp/output/deltalake/")
    String outputDirectory();

    @Key("execution.mode.legacy")
    @DefaultValue("false")
    Boolean executionModeLegacy();
}
