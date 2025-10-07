package com.boozallen.aiops.mda.metamodel.element.java;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.boozallen.aiops.mda.metamodel.element.Relation;
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

    private final Set<String> imports = new TreeSet<>();

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

        addRelationImports();

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
            if (hasDriftPolicy(javaField) || hasEthicsPolicy(javaField)) {
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
            throw new GenerationException("Non-dictionary typed field not supported yet!");
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

    private void addRelationImports() {
        for (Relation relation: wrapped.getRelations()) {
            JavaRecordRelation relationDecorator = new JavaRecordRelation(relation);
            imports.addAll(relationDecorator.getGeneratedClassImport());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Relation> getRelations() {
        List<Relation> wrappedRelations = new ArrayList<>();
        for (Relation relation : wrapped.getRelations()) {
            JavaRecordRelation wrappedRelation = new JavaRecordRelation(relation);
            wrappedRelations.add(wrappedRelation);
        }

        return wrappedRelations;
    }

}
