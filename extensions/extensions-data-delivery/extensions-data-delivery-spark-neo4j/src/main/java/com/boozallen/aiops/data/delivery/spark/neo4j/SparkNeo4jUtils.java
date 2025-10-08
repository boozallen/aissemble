package com.boozallen.aiops.data.delivery.spark.neo4j;

/*-
 * #%L
 * AIOps Foundation::AIOps Data Delivery::Spark::Neo4j
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

import java.util.HashMap;
import java.util.Map;

import org.aeonbits.owner.KrauseningConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

/**
 * Utilities for Spark Neo4j support.
 */
public class SparkNeo4jUtils {

    private static final SparkNeo4jConfig config = KrauseningConfigFactory.create(SparkNeo4jConfig.class);

    public static final String NEO4J_FORMAT = "org.neo4j.spark.DataSource";
    public static final String LABELS_OPTION = "labels";

    /**
     * Returns the options to pass to spark for writing to and reading from
     * neo4j.
     * 
     * @return spark neo4j options
     */
    public static Map<String, String> getSparkOptions() {
        Map<String, String> options = new HashMap<>();
        config.fill(options);

        return options;
    }

    /**
     * Returns the data stored in neo4j with the given labels as a spark
     * dataset.
     * 
     * @param sparkSession
     *            the spark session
     * @param labels
     *            the neo4j label(s) to read the data from
     * @return spark dataset
     */
    public static Dataset<Row> getDataset(SparkSession sparkSession, String... labels) {
        return sparkSession.read()
                .format(NEO4J_FORMAT)
                .options(getSparkOptions())
                .option(LABELS_OPTION, StringUtils.join(labels, ":"))
                .load();
    }

}
