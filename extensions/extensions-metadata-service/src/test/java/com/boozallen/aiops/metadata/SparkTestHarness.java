package com.boozallen.aiops.metadata;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Docker::AIOps Metadata Service
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

import com.boozallen.aissemble.core.cdi.CdiContext;
import org.apache.log4j.Level;
import org.apache.spark.sql.SparkSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Sets up Spark to run within Cucumber. Added as a plugin to the Cucumber runner. (See {@link CucumberTest})
 */
public class SparkTestHarness extends SparkTestBaseHarness {

    /**
     * {@inheritDoc}
     */
    protected void setLogging() {
        // suppress excessive logging from spark and smallrye
        org.apache.log4j.Logger.getLogger("org").setLevel(Level.WARN);
        org.apache.log4j.Logger.getLogger("io").setLevel(Level.ERROR);
    }

    /**
     * {@inheritDoc}
     */
    protected SparkSession.Builder setSparkSessionBuilder() {
        SparkSession.Builder builder = SparkSession.builder();
        builder = builder.master("local[*]");
        builder = builder.appName("spark-metadata-unit-test");
        builder = builder.enableHiveSupport();
        builder = builder.config("spark.driver.host", "localhost");
        builder = builder.config("spark.sql.warehouse.dir", "target/spark-warehouse");

        return builder;
    }

    /**
     * {@inheritDoc}
     */
    protected void configureMessagingChannels() {
        // set up smallrye channels to use in-memory connector so we don't
        // need to bring up kafka for the tests:

    }

    /**
     * {@inheritDoc}
     */
    protected List<CdiContext> getCdiContexts() {
        List<CdiContext> testContexts = new ArrayList<>();
        testContexts.add(new TestCdiContext());
        return testContexts;
    }
}
