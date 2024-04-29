package com.boozallen.aiops.mda.metamodel.element.spark;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.boozallen.aiops.mda.metamodel.element.java.JavaDictionaryType;
import com.boozallen.aiops.mda.metamodel.element.java.JavaRecordField;
import com.boozallen.aiops.mda.metamodel.element.java.JavaRecordFieldType;
import com.boozallen.aiops.mda.metamodel.element.util.JavaElementUtils;
import org.technologybrewery.fermenter.mda.TypeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boozallen.aiops.mda.metamodel.element.Record;
import com.boozallen.aiops.mda.metamodel.element.RecordField;
import com.boozallen.aiops.mda.metamodel.element.java.JavaRecord;

/**
 * Decorates Record with Spark-specific functionality.
 */
public class SparkRecord extends JavaRecord {

    private static final Logger logger = LoggerFactory.getLogger(SparkRecord.class);

    private Set<String> imports = new TreeSet<>();

    private static final String SPARK_DATA_TYPE_IMPORT = "org.apache.spark.sql.types.DataType";

    /**
     * {@inheritDoc}
     */
    public SparkRecord(Record recordToDecorate) {
        super(recordToDecorate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RecordField> getFields() {
        List<RecordField> fields = new ArrayList<>();

        for (RecordField field : super.getFields()) {
            SparkRecordField sparkField = new SparkRecordField(field);
            SparkRecordFieldType fieldType = (SparkRecordFieldType) sparkField.getType();
            if (fieldType.isDictionaryTyped()) {
                fields.add(sparkField);
            } else {
                logger.warn("Spark schema does not support composite type - skip adding field '{}' to Spark schema.",
                        sparkField.getName());
            }
        }

        return fields;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getBaseImports() {
        for (RecordField field : getFields()) {
            SparkRecordField sparkField = (SparkRecordField) field;
            SparkRecordFieldType fieldType = (SparkRecordFieldType) sparkField.getType();
            SparkDictionaryType dictionaryType = (SparkDictionaryType) fieldType.getDictionaryType();
            imports.add(dictionaryType.getFullyQualifiedType());
        }

        imports.add(TypeManager.getFullyQualifiedType("row"));
        imports.add(JavaElementUtils.ROW_FACTORY_IMPORT);
        imports.add(TypeManager.getFullyQualifiedType("dataset"));
        imports.add(SPARK_DATA_TYPE_IMPORT);

        return imports;
    }

    /**
     * Returns the imports for the record base class.
     * @return The import list
     */
    public Set<String> getRecordBaseImports() {
        Set<String> imports = super.getBaseImports();

        for (RecordField field : getFields()) {
            JavaRecordFieldType fieldType = (JavaRecordFieldType) field.getType();
            SparkDictionaryType dictionaryType = (SparkDictionaryType) fieldType.getDictionaryType();
            String simpleTypeImport = TypeManager.getFullyQualifiedType(dictionaryType.getGenericType());
            if (JavaElementUtils.isImportNeeded(simpleTypeImport)) {
                imports.add(simpleTypeImport);
            }
        }
        return imports;
    }
}
