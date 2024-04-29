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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

/**
 * Base schema type to allow for common methods.
 */
public abstract class SparkSchema {

    private static final Logger logger = LoggerFactory.getLogger(SparkSchema.class);

    protected StructType schema;

    protected SparkSchema() {
        schema = new StructType();
    }

    /**
     * Returns the structure type for this schema.
     * 
     * @return structure type
     */
    public StructType getStructType() {
        return schema;
    }

    /**
     * Compares the passed structure to the one represented by the implementation of this interface.
     * 
     * @param schemaToCompare
     *            schema to compare
     * @return whether or not the schemas are equivalent
     */
    public boolean isEquivalent(StructType schemaToCompare) {
        final List<StructField> fieldsList = Arrays.asList(this.getStructType().fields());
        final List<StructField> fieldsListToCompare = Arrays.asList(schemaToCompare.fields());

        List<StructField> mappedFields = fieldsList.stream().sorted(Comparator.comparing(StructField::name))
                .collect(Collectors.toList());

        List<StructField> mappedFieldsToCompare = fieldsListToCompare.stream()
                .sorted(Comparator.comparing(StructField::name)).collect(Collectors.toList());

        return mappedFields.equals(mappedFieldsToCompare);
    }

    /**
     * Converts this schema to a JsonObject.
     * 
     * @return json representation
     */
    public JsonObject toJsonObject() {
        JsonObject jsonObject = new JsonObject();

        for (StructField field : getStructType().fields()) {
            jsonObject.addProperty(field.name(), field.getComment().getOrElse(() -> ""));
        }

        return jsonObject;
    }

    /**
     * Returns the data type for a field in the schema.
     * 
     * @param name
     *            name of the field
     * @return data type of the field
     */
    public DataType getDataType(String name) {
        DataType dataType = null;
        try {
            StructField field = schema.apply(name);
            dataType = field.dataType();
        } catch (IllegalArgumentException e) {
            logger.error("No field named '{}' found in the schema", name, e);
        }

        return dataType;
    }

    /**
     * Adds a field to the schema.
     * 
     * @param name
     *            the name of the field to add
     * @param dataType
     *            the data type of the field to add
     */
    protected void add(String name, DataType dataType) {
        schema = schema.add(name, dataType);
    }

    /**
     * Adds a field to the schema.
     * 
     * @param name
     *            the name of the field to add
     * @param dataType
     *            the data type of the field to add
     * @param nullable
     *            whether the field is nullable
     * @param comment
     *            a description of the field
     */
    protected void add(String name, DataType dataType, boolean nullable, String comment) {
        schema = schema.add(name, dataType, nullable, comment);
    }

    /**
     * Updates the data type of a field in the schema.
     * 
     * @param name
     *            the name of the field to update
     * @param dataType
     *            the new data type of the field
     */
    protected void update(String name, DataType dataType) {
        List<StructField> fields = Arrays.asList(schema.fields());
        if (!fields.isEmpty()) {
            StructType update = new StructType();
            for (StructField field : fields) {
                if (field.name().equals(name)) {
                    boolean nullable = field.nullable();
                    String comment = field.getComment().getOrElse(null);
                    update = update.add(name, dataType, nullable, comment);
                } else {
                    update = update.add(field);
                }

            }

            schema = update;
        }
    }

}
