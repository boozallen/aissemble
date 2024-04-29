package com.boozallen.aiops.data.delivery;

import java.util.Collections;

import org.aeonbits.owner.KrauseningConfigFactory;
import org.apache.spark.sql.SparkSession;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boozallen.aiops.data.delivery.spark.objectstore.ObjectStoreConfig;
import com.boozallen.aiops.data.delivery.spark.objectstore.ObjectStoreValidator;

/*-
 * #%L
 * AIOps Foundation::AIOps Data Delivery::Spark
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class ObjectStoreValidationSteps {
    private static final Logger logger = LoggerFactory.getLogger(ObjectStoreValidationSteps.class);

    private SparkSession sparkSession;

    private WeldContainer weldContainer;

    private ObjectStoreConfig config;

    private ObjectStoreValidator validator;

    @Before("@object-store-validation")
    public void setUp() {
        weldContainer = CdiContainerFactory.getCdiContainer(Collections.singletonList(new TestCdiContext()));
        validator = weldContainer.select(ObjectStoreValidator.class).get();

        sparkSession = SparkSession.builder().master("local[*]").appName("Test").enableHiveSupport()
                .config("spark.driver.host", "localhost").getOrCreate();
    }

    @After("@object-store-validation")
    public void tearDown() {
        if (weldContainer != null) {
            weldContainer.close();
            weldContainer = null;
        }
    }

    @Given("a properties file exists")
    public void aProperiesFileExists() {
        config = KrauseningConfigFactory.create(ObjectStoreConfig.class);
    }

    @Then("the credentials are used to verify object store connectivity")
    public void theCredentialsAreUsedToVerifyObjectStoreConnectivity() {
        validator.verifyObjectStoreConnections(this.sparkSession);
        logger.info(ObjectStoreValidator.result.toString());
        Assert.assertTrue("Expected a result string but found a blank value!", ObjectStoreValidator.result.toString().length() > 0);
    }
}
