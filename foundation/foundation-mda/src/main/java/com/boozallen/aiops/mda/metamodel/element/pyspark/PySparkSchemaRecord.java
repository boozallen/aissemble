package com.boozallen.aiops.mda.metamodel.element.pyspark;

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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boozallen.aiops.mda.metamodel.element.Record;
import com.boozallen.aiops.mda.metamodel.element.RecordField;
import com.boozallen.aiops.mda.metamodel.element.python.PythonRecord;

/**
 * Decorates Record with PySpark-specific functionality.
 */
public class PySparkSchemaRecord extends PythonRecord {

    private static final Logger logger = LoggerFactory.getLogger(PySparkSchemaRecord.class);

    private Set<String> imports = new TreeSet<>();

    /**
     * {@inheritDoc}
     */
    public PySparkSchemaRecord(Record recordToDecorate) {
        super(recordToDecorate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RecordField> getFields() {
        List<RecordField> fields = new ArrayList<>();

        for (RecordField field : wrapped.getFields()) {
            PySparkRecordField pySparkField = new PySparkRecordField(field);
            PySparkRecordFieldType fieldType = (PySparkRecordFieldType) pySparkField.getType();
            if (fieldType.isDictionaryTyped()) {
                fields.add(pySparkField);
            } else {
                logger.warn(
                        "PySpark schema does not support composite type - skip adding field '{}' to PySpark schema.",
                        pySparkField.getName());
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
            PySparkRecordField pySparkField = (PySparkRecordField) field;
            PySparkRecordFieldType fieldType = (PySparkRecordFieldType) pySparkField.getType();
            PySparkDictionaryType dictionaryType = (PySparkDictionaryType) fieldType.getDictionaryType();
            String dictionaryTypeImport = dictionaryType.getSimpleTypeImport();
            if (StringUtils.isNotBlank(dictionaryTypeImport)) {
                imports.add(dictionaryTypeImport);
            }
        }

        return imports;
    }

}
