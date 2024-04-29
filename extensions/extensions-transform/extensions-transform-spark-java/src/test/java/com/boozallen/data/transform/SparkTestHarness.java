package com.boozallen.data.transform;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Transform::Spark::Java
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cucumber.plugin.EventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.TestRunFinished;
import io.cucumber.plugin.event.TestRunStarted;

public class SparkTestHarness implements EventListener {

    private static final Logger logger = LoggerFactory.getLogger(SparkTestHarness.class);

    private static SparkSession spark;

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        // setup before all cucumber tests
        publisher.registerHandlerFor(TestRunStarted.class, handler -> {
            logger.debug("Starting Spark test session...");
            long start = System.currentTimeMillis();

            spark = SparkSession.builder()
                    .master("local[*]")
                    .appName("DataTransformSteps")
                    .enableHiveSupport()
                    .config("spark.driver.host", "localhost")
                    .getOrCreate();

            long stop = System.currentTimeMillis();
            logger.debug("Started Spark test session in {}ms", stop - start);
        });

        // cleanup after all cucumber tests
        publisher.registerHandlerFor(TestRunFinished.class, handler -> {
            logger.debug("Stopping Spark test session...");
            long start = System.currentTimeMillis();

            spark.close();

            long stop = System.currentTimeMillis();
            logger.debug("Stopped Spark test session in {}ms", stop - start);
        });
    }

    public static SparkSession getSparkSession() {
        return spark;
    }

}
