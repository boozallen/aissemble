package com.boozallen.aiops.mda.metamodel.element.python;

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
import org.technologybrewery.fermenter.mda.generator.GenerationException;

import com.boozallen.aiops.mda.metamodel.element.BaseRecordDecorator;
import com.boozallen.aiops.mda.metamodel.element.Record;
import com.boozallen.aiops.mda.metamodel.element.RecordField;
import com.boozallen.aiops.mda.metamodel.element.java.JavaRecordField;
import com.boozallen.aiops.mda.metamodel.element.util.PythonElementUtils;

/**
 * Decorates Record with Python-specific functionality.
 */
public class PythonRecord extends BaseRecordDecorator {

    private Set<String> imports = new TreeSet<>();

    /**
     * {@inheritDoc}
     */
    public PythonRecord(Record recordToDecorate) {
        super(recordToDecorate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RecordField> getFields() {
        List<RecordField> fields = new ArrayList<>();

        for (RecordField field : super.getFields()) {
            fields.add(new PythonRecordField(field));
        }

        return fields;
    }

    /**
     * Returns the record name formatted into lowercase with underscores (Python
     * naming convention).
     * 
     * @return the record name formatted into lowercase with underscores
     */
    public String getSnakeCaseName() {
        return PythonElementUtils.getSnakeCaseValue(getName());
    }

    /**
     * Returns the base Python imports for this record.
     * 
     * @return base imports
     */
    public Set<String> getBaseImports() {
        for (RecordField field : getFields()) {
            PythonRecordField pythonField = (PythonRecordField) field;
            addFieldImports(pythonField, false);
        }

        return imports;
    }

    /**
     * Returns the imports for this python record's enum.
     * 
     * @return enum imports
     */
    public Set<String> getEnumImports() {
        for (RecordField field : getFields()) {
            PythonRecordField pythonField = (PythonRecordField) field;
            if (hasDriftPolicy(pythonField) || hasEthicsPolicy(pythonField) || hasProtectionPolicy(pythonField)) {
                addFieldImports(pythonField, true);
            }
        }

        return imports;
    }
    
    private boolean hasDriftPolicy(PythonRecordField pythonField) {
        return StringUtils.isNotBlank(pythonField.getDriftPolicy()) && !pythonField.hasOverriddenDriftPolicy();
    }

    private boolean hasEthicsPolicy(PythonRecordField oythonField) {
        return StringUtils.isNotBlank(oythonField.getEthicsPolicy()) && !oythonField.hasOverriddenEthicsPolicy();
    }

    private boolean hasProtectionPolicy(PythonRecordField pythonField) {
        return StringUtils.isNotBlank(pythonField.getProtectionPolicy()) && !pythonField.hasOverriddenProtectionPolicy();
    }

    private void addFieldImports(PythonRecordField field, boolean forEnum) {
        PythonRecordFieldType fieldType = (PythonRecordFieldType) field.getType();
        if (fieldType.isDictionaryTyped()) {
            PythonDictionaryType dictionaryType = (PythonDictionaryType) fieldType.getDictionaryType();
            addDictionaryTypeImports(dictionaryType, forEnum);
        } else if (fieldType.isCompositeTyped()) {
            throw new GenerationException("Composite typed field not supported yet!");
        }
    }

    private void addDictionaryTypeImports(PythonDictionaryType dictionaryType, boolean forEnum) {
        if (dictionaryType.isComplex()) {
            String generatedClassImport = dictionaryType.getGeneratedClassImport();
            imports.add(generatedClassImport);
        }

        if (!forEnum) {
            String simpleTypeImport = dictionaryType.getSimpleTypeImport();
            if (StringUtils.isNotBlank(simpleTypeImport)) {
                imports.add(simpleTypeImport);
            }
        }
    }

}
