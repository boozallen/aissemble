package com.boozallen.aiops.mda.generator.config.deployment.spark;

/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */


import javax.inject.Singleton;
import java.io.IOException;
import java.util.Properties;

/**
 * Config class giving access to Spark dependency versions, as reflected in the Maven POM.  This mechanism allows
 * for a single source of truth and eases maintenance.  The maven resources plugin filters properties into the props
 * file (stub located at src/main/resources/properties/dependencies.properties), with the output file reflected in
 * PROPERTIES_PATH within the resultant JAR.
 * Because we are pulling specifically from the classpath, and don't want to respect KRAUSENING_BASE,
 * we cannot leverage Krausening here.  Instead, we produce a singleton reflecting the properties directly.
 */
@Singleton
public final class SparkDependencyConfiguration extends Properties {
    private static final String PROPERTIES_PATH = "/dependencies.properties";

    // Singleton as we will never need to react to file changes after build-time, and all props are read-only.
    private static final SparkDependencyConfiguration instance;
    static {
        try {
            instance = new SparkDependencyConfiguration();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return singleton SparkDependencyConfiguration containing version settings.
     */
    public static SparkDependencyConfiguration getInstance() {
        return instance;
    }

    private SparkDependencyConfiguration() throws IOException {
        load(getClass().getResourceAsStream(PROPERTIES_PATH));
    }

    /**
     * All subsequent methods are merely accessors for properties defined in the prop stub (see class-level doc).
     */

    public String getSparkVersion() {
        return getProperty("version.spark");
    }

    public String getDeltaVersion() {
        return getProperty("version.delta");
    }

    public String getElasticSearchVersion() {
        return getProperty("version.elasticsearch");
    }

    public String getSedonaVersion() {
        return getProperty("version.sedona");
    }

    public String getGeotoolsVersion() {
        return getProperty("version.geotools");
    }

    public String getPostgresqlVersion() {
        return getProperty("version.postgresql");
    }

    public String getMysqlConnectorVersion() {
        return getProperty("version.mysql");
    }

    public String getHadoopVersion() {
        return getProperty("version.hadoop");
    }

    public String getNeo4jVersion() {
        return getProperty("version.neo4j");
    }

    public String getAwsSdkBundleVersion() {
        return getProperty("version.aws.sdk.bundle");
    }
}
