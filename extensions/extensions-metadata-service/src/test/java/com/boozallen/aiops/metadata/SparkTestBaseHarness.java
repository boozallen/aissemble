package com.boozallen.aiops.metadata;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Docker::AIOps Metadata Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.core.cdi.CdiContext;
import com.boozallen.aiops.data.delivery.spark.SparkConfig;

import io.cucumber.plugin.EventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.TestRunFinished;
import io.cucumber.plugin.event.TestRunStarted;
import io.smallrye.reactive.messaging.providers.connectors.InMemoryConnector;
import org.aeonbits.owner.KrauseningConfigFactory;
import org.apache.commons.io.FileUtils;
import org.apache.spark.sql.SparkSession;
import org.jboss.weld.environment.se.WeldContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Sets up base functionality for Spark to run within Cucumber.  Leaving this separate even though it is not generated
 * here in order to make it easier to maintain correlation with generated versions over time.
 */
public abstract class SparkTestBaseHarness implements EventListener {

    private static final Logger logger = LoggerFactory.getLogger(SparkTestBaseHarness.class);
    protected static final SparkConfig config = KrauseningConfigFactory.create(SparkConfig.class);

    protected static SparkSession spark;
    protected WeldContainer container;

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        // setup before all cucumber tests
        publisher.registerHandlerFor(TestRunStarted.class, handler -> {
            setLogging();
            applyBeforeTests();

            // one spark session for all tests
            long sparkStart = System.currentTimeMillis();
            SparkSession.Builder builder = setSparkSessionBuilder();
            spark = builder.getOrCreate();
            long sparkStop = System.currentTimeMillis();
            logger.debug("Started Spark test session in {}ms", (sparkStop - sparkStart));

            long messagingStart = System.currentTimeMillis();
            configureMessagingChannels();
            long messagingStop = System.currentTimeMillis();
            logger.debug("Started Messaging test resources in {}ms", (messagingStop - messagingStart));

            // one container to run smallrye for all tests
            long cdiStart = System.currentTimeMillis();
            List<CdiContext> testContexts = getCdiContexts();
            container = CdiContainerFactory.getCdiContainer(testContexts);
            long cdiStop = System.currentTimeMillis();
            logger.debug("Started CDI test resources in {}ms", (cdiStop - cdiStart));
        });

        // cleanup after all cucumber tests
        publisher.registerHandlerFor(TestRunFinished.class, handler -> {

            long tearDownStart = System.currentTimeMillis();
            InMemoryConnector.clear();
            if (container != null) {
                container.shutdown();
            }
            if (spark != null) {
                spark.close();
            }
            applyAfterTests();
            long tearDownStop = System.currentTimeMillis();
            logger.debug("Stopped test resources in {}ms", (tearDownStop - tearDownStart));

            try {
                FileUtils.deleteDirectory(new File(config.outputDirectory()));
            } catch (IOException e) {
                logger.error("failed to delete delta lake table: {}", e.getMessage());
            }
        });
    }

    public static SparkSession getSparkSession() {
        return spark;
    }

    /**
     * Tweak logging within Spark container, if desired.
     */
    protected void setLogging() {
    }

    /**
     * Apply additional setup before the Cucumber tests, if desired.
     */
    protected void applyBeforeTests() {
    }

    /**
     * Apply additional cleanup actions after the Cucumber tests, if desired.
     */
    protected void applyAfterTests() {
    }

    /**
     * Provides an opportunity to configure the {@link SparkSession.Builder} that
     * will be used to create the Cucumber test spark session.
     *
     * @return spark session builder with any desired configurations
     */
    protected abstract SparkSession.Builder setSparkSessionBuilder();

    /**
     * Configure any channels used for messaging. With ReativeMessaging, you can
     * override channels that use third party tools (e.g., Kafka, Apache ActiveMQ)
     * to instead use local, in-memory channels so tests can remain unit vice
     * integration tests.
     */
    protected abstract void configureMessagingChannels();

    /**
     * Add any test-specific CDI artifacts via a {@link CdiContext} implementation.
     *
     * @return list of test contexts
     */
    protected abstract List<CdiContext> getCdiContexts();

}
