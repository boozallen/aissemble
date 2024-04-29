package com.boozallen.aiops.data.delivery.messaging.pipeline.steps;

/*-
 * #%L
 * AIOps Foundation::AIOps Data Delivery::Spark
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.data.delivery.spark.AbstractDataAction;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.spark.sql.SparkSession;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTestStep extends AbstractDataAction {
    protected List<String> received = new ArrayList<>();
    protected List<String> sent = new ArrayList<>();
    static {
        SparkSession.builder()
                .master("local[*]")
                .appName("Test")
                .enableHiveSupport()
                .config("spark.driver.host", "localhost")
                .getOrCreate();    }

    protected AbstractTestStep(String subject, String action) {
        super(subject, action);
    }

    public abstract String getIncomingChannel();

    public abstract String getOutgoingChannel();

    public String executeStep(String input) {
        received.add(input);
        return generateOutput();
    }

    public List<String> received() {
        return received;
    }

    public List<String> sent() {
        return sent;
    }

    protected String generateOutput() {
        String output = "TestOutput-" + RandomStringUtils.randomAlphanumeric(5);
        sent.add(output);
        return output;
    }
 }
