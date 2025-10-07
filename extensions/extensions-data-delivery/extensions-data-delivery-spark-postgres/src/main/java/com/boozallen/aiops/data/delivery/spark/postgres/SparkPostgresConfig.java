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
