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

import org.aeonbits.owner.KrauseningConfig;

/**
 * Configurations for Spark Postgres support.
 */
@KrauseningConfig.KrauseningSources("spark-postgres.properties")
public interface SparkPostgresConfig extends KrauseningConfig {

    /**
     * The JDBC URL for the database connection.
     * 
     * @return JDBC URL
     */
    @Key("jdbc.url")
    @DefaultValue("jdbc:postgresql://postgres:5432/db")
    String jdbcUrl();

    /**
     * The JDBC driver class name.
     * 
     * @return JDBC driver
     */
    @Key("jdbc.driver")
    @DefaultValue("org.postgresql.Driver")
    String jdbcDriver();

    /**
     * The Postgres user.
     * 
     * @return Postgres user
     */
    @Key("postgres.user")
    @DefaultValue("postgres")
    String postgresUser();

    /**
     * The password for the Postgres user.
     * 
     * @return Postgres password
     */
    @Key("postgres.password")
    @DefaultValue("password")
    String postgresPassword();

}
