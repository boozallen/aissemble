package com.boozallen.aiops.data.delivery.spark.neo4j;

/*-
 * #%L
 * AIOps Foundation::AIOps Data Delivery::Spark::Neo4j
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.aeonbits.owner.KrauseningConfig;

/**
 * Configurations for Spark Neo4j support.
 * 
 * See Neo4j Spark Connector documentation for more information:
 * https://neo4j.com/docs/spark/current/configuration/
 */
@KrauseningConfig.KrauseningSources("spark-neo4j.properties")
public interface SparkNeo4jConfig extends KrauseningConfig {

    /**
     * The url of the Neo4j instance to connect to.
     * 
     * @return url
     */
    @Key("url")
    @DefaultValue("bolt://neo4j:7687")
    String url();

    /**
     * The authentication method to be used: none, basic, kerberos, custom.
     * 
     * @return authentication type
     */
    @Key("authentication.type")
    @DefaultValue("basic")
    String authenticationType();

    /**
     * Username to use for basic authentication type.
     * 
     * @return basic authentication username
     */
    @Key("authentication.basic.username")
    @DefaultValue("neo4j")
    String authenticationBasicUsername();

    /**
     * Password to use for basic authentication type.
     * 
     * @return basic authentication password
     */
    @Key("authentication.basic.password")
    @DefaultValue("p455w0rd")
    String authenticationBasicPassword();

    /**
     * The kerberos authentication ticket for kerberos authentication type.
     * 
     * @return kerberos authentication ticket
     */
    @Key("authentication.kerberos.ticket")
    String authenticationKerberosTicket();

    /**
     * Principal for custom authentication type.
     * 
     * @return custom authentication principal
     */
    @Key("authentication.custom.principal")
    String authenticationCustomPrincipal();

    /**
     * Credentials for custom authentication type.
     * 
     * @return custom authentication credentials
     */
    @Key("authentication.custom.credentials")
    String authenticationCustomCredentials();

    /**
     * Realm for custom authentication type.
     * 
     * @return custom authentication realm
     */
    @Key("authentication.custom.realm")
    String authenticationCustomRealm();

    /**
     * Whether encryption should be enabled.
     * 
     * @return true if encryption should be enabled
     */
    @Key("encryption.enabled")
    String encryptionEnabled();

    /**
     * Certificate trust strategy when encryption is enabled.
     * 
     * @return certificate trust strategy
     */
    @Key("encryption.trust.strategy")
    String encryptionTrustStrategy();

    /**
     * Certificate path when encryption is enabled.
     * 
     * @return certificate path
     */
    @Key("encryption.ca.certificate.path")
    String encryptionCaCertificatePath();

    /**
     * Connection lifetime in milliseconds.
     * 
     * @return connection lifetime
     */
    @Key("connection.max.lifetime.msecs")
    String connectionMaxLifetimeMsecs();

    /**
     * Liveness check timeout in milliseconds.
     * 
     * @return liveness check timeout
     */
    @Key("connection.liveness.timeout.msecs")
    String connectionLivenessTimeoutMsecs();

    /**
     * Connection acquisition timeout in milliseconds.
     * 
     * @return connection acquisition timeout
     */
    @Key("connection.acquisition.timeout.msecs")
    String connectionAcquisitionTimeoutMsecs();

    /**
     * Connection timeout in milliseconds.
     * 
     * @return collection timeout
     */
    @Key("connection.timeout.msecs")
    String connectionTimeoutMsecs();

}
