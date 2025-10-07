package com.boozallen.aiops.mda.metamodel.element.python;

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

import com.boozallen.aiops.mda.metamodel.element.BaseRecordDecorator;
import com.boozallen.aiops.mda.metamodel.element.Record;
import com.boozallen.aiops.mda.metamodel.element.RecordField;
import com.boozallen.aiops.mda.metamodel.element.util.PythonElementUtils;

/**
 * Decorates Record with Python-specific functionality.
 */
public class PythonRecord extends BaseRecordDecorator {

    private final Set<String> imports = new TreeSet<>();

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

        addRelationImports();

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
            if (hasDriftPolicy(pythonField) || hasEthicsPolicy(pythonField)) {
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

    private void addFieldImports(PythonRecordField field, boolean forEnum) {
        PythonRecordFieldType fieldType = (PythonRecordFieldType) field.getType();
        if (fieldType.isDictionaryTyped()) {
            PythonDictionaryType dictionaryType = (PythonDictionaryType) fieldType.getDictionaryType();
            addDictionaryTypeImports(dictionaryType, forEnum);
        }
    }

    private void addRelationImports() {
        for (Relation relation: wrapped.getRelations()) {
            PythonRecordRelation relationDecorator = new PythonRecordRelation(relation);
            imports.addAll(relationDecorator.getGeneratedClassImport());
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

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Relation> getRelations() {
        List<Relation> wrappedRelations = new ArrayList<>();
        for (Relation relation : wrapped.getRelations()) {
            PythonRecordRelation wrappedRelation = new PythonRecordRelation(relation);
            wrappedRelations.add(wrappedRelation);
        }

        return wrappedRelations;
    }

}
