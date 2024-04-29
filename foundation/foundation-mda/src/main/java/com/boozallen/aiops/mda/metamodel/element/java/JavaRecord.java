package com.boozallen.aiops.mda.metamodel.element.java;

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
import org.technologybrewery.fermenter.mda.TypeManager;
import org.technologybrewery.fermenter.mda.generator.GenerationException;

import com.boozallen.aiops.mda.metamodel.element.BaseRecordDecorator;
import com.boozallen.aiops.mda.metamodel.element.Record;
import com.boozallen.aiops.mda.metamodel.element.RecordField;
import com.boozallen.aiops.mda.metamodel.element.util.JavaElementUtils;

/**
 * Decorates Record with Java-specific functionality.
 */
public class JavaRecord extends BaseRecordDecorator {

    private Set<String> imports = new TreeSet<>();

    /**
     * {@inheritDoc}
     */
    public JavaRecord(Record recordToDecorate) {
        super(recordToDecorate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RecordField> getFields() {
        List<RecordField> fields = new ArrayList<>();

        for (RecordField field : super.getFields()) {
            fields.add(new JavaRecordField(field));
        }

        return fields;
    }

    /**
     * Returns the base imports for this java record.
     *
     * @return base imports
     */
    public Set<String> getBaseImports() {
        for (RecordField field : getFields()) {
            JavaRecordField javaField = (JavaRecordField) field;
            addFieldImports(javaField);
        }

        imports.add(JavaElementUtils.MAP_IMPORT);
        imports.add(JavaElementUtils.HASH_MAP_IMPORT);

        return imports;
    }

    /**
     * Returns the imports for this java record's enum.
     * 
     * @return enum imports
     */
    public Set<String> getEnumImports() {
        for (RecordField field : getFields()) {
            JavaRecordField javaField = (JavaRecordField) field;
            if (hasDriftPolicy(javaField) || hasEthicsPolicy(javaField) || hasProtectionPolicy(javaField)) {
                addDictionaryTypeImports(javaField, true);
            }
        }

        imports.add(TypeManager.getFullyQualifiedType("list"));
        imports.add(JavaElementUtils.ARRAY_LIST_IMPORT);
        imports.add(JavaElementUtils.STRING_UTILS_IMPORT);

        return imports;
    }

    private boolean hasDriftPolicy(JavaRecordField javaField) {
        return StringUtils.isNotBlank(javaField.getDriftPolicy()) && !javaField.hasOverriddenDriftPolicy();
    }

    private boolean hasEthicsPolicy(JavaRecordField javaField) {
        return StringUtils.isNotBlank(javaField.getEthicsPolicy()) && !javaField.hasOverriddenEthicsPolicy();
    }

    private boolean hasProtectionPolicy(JavaRecordField javaField) {
        return StringUtils.isNotBlank(javaField.getProtectionPolicy()) && !javaField.hasOverriddenProtectionPolicy();
    }

    /**
     * Returns the data-access related imports for this java record.
     * 
     * @return data-access imports
     */
    public Set<String> getDataAccessImports() {
        for (RecordField field : getFields()) {
            JavaRecordField javaField = (JavaRecordField) field;
            addSimpleTypeImport(javaField);
        }

        return imports;
    }

    private void addFieldImports(JavaRecordField field) {
        addDictionaryTypeImports(field, false);

        if (field.isRequired() || field.hasValidation()) {
            imports.add(JavaElementUtils.VALIDATION_EXCEPTION_IMPORT);
        }
    }

    private void addDictionaryTypeImports(JavaRecordField field, boolean forEnum) {
        JavaRecordFieldType fieldType = (JavaRecordFieldType) field.getType();
        if (fieldType.isDictionaryTyped()) {
            JavaDictionaryType dictionaryType = (JavaDictionaryType) fieldType.getDictionaryType();
            if (dictionaryType.isComplex()) {
                String dictionaryTypePackage = fieldType.getPackage();
                String generatedClassImport = dictionaryTypePackage + "." + dictionaryType.getCapitalizedName();
                imports.add(generatedClassImport);
            }

            if (!forEnum) {
                addSimpleTypeImport(field);
            }
        } else {
            throw new GenerationException("Composite typed field not supported yet!");
        }
    }

    private void addSimpleTypeImport(JavaRecordField field) {
        JavaRecordFieldType fieldType = (JavaRecordFieldType) field.getType();
        JavaDictionaryType dictionaryType = (JavaDictionaryType) fieldType.getDictionaryType();
        String simpleTypeImport = dictionaryType.getFullyQualifiedType();
        if (JavaElementUtils.isImportNeeded(simpleTypeImport)) {
            imports.add(dictionaryType.getFullyQualifiedType());
        }
    }

}
