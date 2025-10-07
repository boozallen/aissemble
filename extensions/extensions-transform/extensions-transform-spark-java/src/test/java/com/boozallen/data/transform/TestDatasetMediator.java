package com.boozallen.data.transform;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Transform::Spark::Java
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
