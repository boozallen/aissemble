package com.boozallen.aiops.data.delivery.spark.postgres;

/*-
 * #%L
 * AIOps Foundation::AIOps Data Delivery::Spark::Postgres
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

import java.util.Properties;

import org.aeonbits.owner.KrauseningConfigFactory;

/**
 * Utilities for Spark Postgres support.
 */
public class SparkPostgresUtils {

    private static final SparkPostgresConfig config = KrauseningConfigFactory.create(SparkPostgresConfig.class);

    private static final String USER = config.postgresUser();
    private static final String PASSWORD = config.postgresPassword();
    private static final String JDBC_URL = config.jdbcUrl();
    private static final String JDBC_DRIVER = config.jdbcDriver();

    private SparkPostgresUtils() {
    }

    /**
     * Returns the JDBC properties for the Postgres connection.
     * 
     * @return JDBC properties
     */
    public static Properties getJdbcProperties() {
        Properties properties = new Properties();
        properties.put("driver", JDBC_DRIVER);
        properties.put("user", USER);
        properties.put("password", PASSWORD);

        return properties;
    }

    /**
     * Returns the JDBC URL for the Postgres connection.
     * 
     * @return Postgres JDBC URL
     */
    public static String getJdbcUrl() {
        return JDBC_URL;
    }

}
