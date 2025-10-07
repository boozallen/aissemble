package com.boozallen.data.transform.spark.mediator;

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
