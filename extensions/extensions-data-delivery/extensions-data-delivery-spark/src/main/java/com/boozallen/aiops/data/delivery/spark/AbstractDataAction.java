package com.boozallen.aiops.data.delivery.spark;

/*-
 * #%L
 * AIOps Foundation::AIOps Data Delivery::Spark
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

import org.aeonbits.owner.KrauseningConfigFactory;
import org.apache.log4j.Level;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains the general concepts needed to perform a base AIOps Reference Architecture Data Action. A Data Action is a
 * step within a Data Flow.
 */
public abstract class AbstractDataAction {

    protected static final SparkConfig config = KrauseningConfigFactory.create(SparkConfig.class);
    private static final Logger logger = LoggerFactory.getLogger(AbstractDataAction.class);

    protected String subject;
    protected String action;

    protected final SparkSession sparkSession;

    protected AbstractDataAction(String subject, String action) {
        this.subject = subject;
        this.action = action;

        tailorSparkLoggingLevels();

        this.sparkSession = this.inferSparkSession();
    }

    private SparkSession inferSparkSession() {
        SparkSession.Builder builder = SparkSession.builder();
        if (config.executionModeLegacy()) {
            logger.warn("Forcing legacy execution...");
            builder = builder
                    .master("local[*]")
                    .config("spark.driver.host", "localhost");
        }
        return builder.getOrCreate();
    }

    /**
     * Allows Spark logging levels to be tailored to prevent excessive logging.
     */
    protected void tailorSparkLoggingLevels() {
        // suppress excessive logging from spark and smallrye
        org.apache.log4j.Logger.getLogger("org").setLevel(Level.WARN);
        org.apache.log4j.Logger.getLogger("io").setLevel(Level.ERROR);
    }
}
