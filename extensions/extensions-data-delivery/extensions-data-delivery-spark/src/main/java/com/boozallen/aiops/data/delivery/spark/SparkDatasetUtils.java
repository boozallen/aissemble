package com.boozallen.aiops.data.delivery.spark;

/*-
 * #%L
 * AIOps Foundation::AIOps Data Delivery::Spark
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for useful Spark dataset and row operations.
 */
public class SparkDatasetUtils {

    private static final Logger logger = LoggerFactory.getLogger(SparkDatasetUtils.class);

    private SparkDatasetUtils() {
    }

    /**
     * Gets a field's value from a Spark row.
     * 
     * @param row
     *            the row to check
     * @param field
     *            the field whose value to get
     * @return value object
     */
    public static Object getRowValue(Row row, String field) {
        Object value;
        try {
            int index = row.fieldIndex(field);
            value = row.get(index);
        } catch (UnsupportedOperationException | IllegalArgumentException e) {
            logger.error("Unable to get value for field '{}'", field, e);
            value = null;
        }

        return value;
    }

}
