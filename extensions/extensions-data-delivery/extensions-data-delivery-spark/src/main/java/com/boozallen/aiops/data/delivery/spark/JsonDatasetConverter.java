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

import java.util.Arrays;
import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performs json conversion to and form Spark {@link Dataset}s.
 */
public class JsonDatasetConverter {
    
    private static final Logger logger = LoggerFactory.getLogger(JsonDatasetConverter.class);

    private static final Encoder<String> STRING_ENCODERS = Encoders.STRING();
    private SparkSession spark;

    public JsonDatasetConverter(SparkSession spark) {
        this.spark = spark;
    }

    /**
     * Converts a json string into a spark dataset.
     * 
     * @param json
     *            the json string to convert
     * @param schema
     *            the schema of the dataset to return
     * @return the json string converted to a dataset
     */
    public Dataset<Row> toDataset(String json, SparkSchema schema) {
        logger.trace(json);
        
        // create dataset representation of the json string
        List<String> listOfElements = Arrays.asList(json);
        Dataset<String> jsonDataset = spark.createDataset(listOfElements, STRING_ENCODERS);

        // convert the json dataset into the given schema dataset
        StructType structType = schema.getStructType();
        return spark.read().schema(structType).json(jsonDataset);
    }

    /**
     * Converts a dataset into a json string.
     * 
     * @param dataset
     *            the dataset to convert
     * @return the json string representation of the dataset
     */
    public String toJson(Dataset<Row> dataset) {
        Dataset<String> stringDataset = dataset.toJSON();
        List<String> stringList = stringDataset.collectAsList();
        return stringList.toString();
    }

}
