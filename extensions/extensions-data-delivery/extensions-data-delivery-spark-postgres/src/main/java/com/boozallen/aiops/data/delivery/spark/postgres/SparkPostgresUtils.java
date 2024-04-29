package com.boozallen.aiops.data.delivery.spark.postgres;

/*-
 * #%L
 * AIOps Foundation::AIOps Data Delivery::Spark::Postgres
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
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
