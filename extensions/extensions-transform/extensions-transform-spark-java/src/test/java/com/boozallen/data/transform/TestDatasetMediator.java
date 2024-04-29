package com.boozallen.data.transform;

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
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boozallen.data.transform.spark.mediator.AbstractDatasetMediator;

import static org.apache.spark.sql.functions.col;

/**
 * Test dataset mediator for unit test purposes.
 */
public class TestDatasetMediator extends AbstractDatasetMediator<Row> {

    private static final Logger logger = LoggerFactory.getLogger(TestDatasetMediator.class);

    public static final String COLUMN = "testColumn";
    public static final DataType DATA_TYPE = DataTypes.IntegerType;

    /**
     * {@inheritDoc}
     */
    @Override
    public Dataset<Row> transform(Dataset<Row> input, Properties properties) {
        logger.info("Dataset before transformation:");
        input.printSchema();
        input.show(false);

        Dataset<Row> output = input.withColumn(COLUMN, col(COLUMN).cast(DATA_TYPE));

        logger.info("Dataset after transformation:");
        output.printSchema();
        output.show(false);

        return output;
    }

}
