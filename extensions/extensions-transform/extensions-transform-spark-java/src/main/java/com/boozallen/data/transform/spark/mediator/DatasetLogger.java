package com.boozallen.data.transform.spark.mediator;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Transform::Spark::Java
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.Properties;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link DatasetLogger} class is an example {@link AbstractDatasetMediator}
 * that logs a sample of a dataset and its schema.
 * 
 * @author Booz Allen Hamilton
 * 
 */
public class DatasetLogger extends AbstractDatasetMediator<Row> {

    private static final Logger logger = LoggerFactory.getLogger(DatasetLogger.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public Dataset<Row> transform(Dataset<Row> input, Properties properties) {
        logger.info("Below is a sample of the input data:");
        input.show(5, false);

        logger.info("With the following schema:");
        input.printSchema();

        return input;
    }

}
