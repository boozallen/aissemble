package com.boozallen.data.transform;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Transform::Spark::Java
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

            if (spark != null) {
                spark.close();
            }

            long stop = System.currentTimeMillis();
            logger.debug("Stopped Spark test session in {}ms", stop - start);
        });
    }

    public static SparkSession getSparkSession() {
        return spark;
    }

}
