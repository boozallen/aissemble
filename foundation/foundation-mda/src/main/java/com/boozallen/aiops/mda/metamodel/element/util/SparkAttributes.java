package com.boozallen.aiops.mda.metamodel.element.util;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boozallen.aiops.mda.metamodel.element.DictionaryType;
import com.boozallen.aiops.mda.metamodel.element.RecordField;
import com.boozallen.aiops.mda.metamodel.element.RecordFieldType;
import com.boozallen.aiops.mda.metamodel.element.Validation;

/**
 * Class for common Spark-related attributes.
 */
public class SparkAttributes {

    private static final Logger logger = LoggerFactory.getLogger(SparkAttributes.class);

    private static final Integer DEFAULT_DECIMAL_PRECISION = 10;
    private static final Integer DEFAULT_DECIMAL_SCALE = 2;

    private final RecordField field;

    public SparkAttributes(RecordField field) {
        this.field = field;
    }

    /**
     * Whether the Spark field is nullable.
     * 
     * @return true if the Spark field is nullable
     */
    public boolean isNullable() {
        return field.isRequired() == null || !field.isRequired();
    }

    /**
     * Whether or not the Spark field's type is a decimal type.
     * 
     * @return true if the field type is a decimal type
     */
    public boolean isDecimalType() {
        boolean isDecimalType = false;

        RecordFieldType fieldType = field.getType();
        if (fieldType.isDictionaryTyped()) {
            DictionaryType dictionaryType = fieldType.getDictionaryType();
            String simpleType = dictionaryType.getSimpleType();
            isDecimalType = simpleType.contains("decimal");
        }

        return isDecimalType;
    }

    /**
     * Returns default decimal precision value.
     * 
     * @return default decimal precision value
     */
    public Integer getDefaultDecimalPrecision() {
        return DEFAULT_DECIMAL_PRECISION;
    }

    /**
     * Returns the scale validation value if the Spark field's type is a decimal
     * type.
     * 
     * @return decimal scale value
     */
    public Integer getDecimalScale() {
        Integer scale = null;

        if (isDecimalType()) {
            Validation validation = field.getType().getDictionaryType().getValidation();
            if (validation != null && validation.getScale() != null) {
                scale = validation.getScale();
            } else {
                scale = DEFAULT_DECIMAL_SCALE;
            }
        }

        return scale;
    }

    /**
     * Returns the column name for the Spark field.
     * 
     * @return column name
     */
    public String getColumnName() {
        String columnName;

        if (StringUtils.isNotBlank(field.getColumn())) {
            columnName = field.getColumn();
        } else {
            columnName = field.getName();
            logger.warn("No column value found for field '{}' - defaulting Spark schema column name to field name",
                    columnName);
        }

        return columnName;
    }

}
