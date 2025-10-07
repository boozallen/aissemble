package com.boozallen.aiops.mda.metamodel.element.spark;

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

import com.boozallen.aiops.mda.metamodel.element.RecordField;
import com.boozallen.aiops.mda.metamodel.element.RecordFieldType;
import com.boozallen.aiops.mda.metamodel.element.java.JavaRecordField;
import com.boozallen.aiops.mda.metamodel.element.util.SparkAttributes;
import org.technologybrewery.fermenter.mda.TypeManager;

/**
 * Decorates RecordField with Spark-specific functionality.
 */
public class SparkRecordField extends JavaRecordField {

    /**
     * {@inheritDoc}
     */
    public SparkRecordField(RecordField recordFieldToDecorate) {
        super(recordFieldToDecorate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecordFieldType getType() {
        return new SparkRecordFieldType(super.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return StringUtils.isNotBlank(super.getDescription()) ? super.getDescription() : "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getShortType() {
        SparkRecordFieldType fieldType = (SparkRecordFieldType) getType();
        SparkDictionaryType dictionaryType = (SparkDictionaryType) fieldType.getDictionaryType();

        return dictionaryType.getShortType();
    }

    /**
     * Gets the type of a non-spark record field, for the purposes of mapping spark rows to curstom records
     * and vice-versa.
     * @return The type name
     */
    public String getGenericType() {
        SparkRecordFieldType fieldType = (SparkRecordFieldType) getType();
        SparkDictionaryType dictionaryType = (SparkDictionaryType) fieldType.getDictionaryType();

        return dictionaryType.isComplex() ? dictionaryType.getCapitalizedName()
                : TypeManager.getShortType(dictionaryType.getGenericType());
    }

    /**
     * Returns Spark-related attributes for this field.
     * 
     * @return Spark-related attributes
     */
    public SparkAttributes getSparkAttributes() {
        return new SparkAttributes(this);
    }

}
