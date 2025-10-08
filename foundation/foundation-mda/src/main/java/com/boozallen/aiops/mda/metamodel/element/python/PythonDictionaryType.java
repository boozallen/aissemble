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

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;

import com.boozallen.aiops.mda.metamodel.element.BaseDictionaryTypeDecorator;
import com.boozallen.aiops.mda.metamodel.element.DictionaryType;
import com.boozallen.aiops.mda.metamodel.element.util.PythonElementUtils;

/**
 * Decorates DictionaryType with Python-specific functionality.
 */
public class PythonDictionaryType extends BaseDictionaryTypeDecorator {

    private static final List<String> VALIDATABLE_TYPES = Arrays.asList("Decimal", "float", "int", "str");

    /**
     * {@inheritDoc}
     */
    public PythonDictionaryType(DictionaryType dictionaryTypeToDecorate) {
        super(dictionaryTypeToDecorate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSimpleType() {
        return super.getSimpleType() + "-python";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        super.validate();
        validateValidations(VALIDATABLE_TYPES, isString(), isDecimal());
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
     * Returns the import for this dictionary type.
     * 
     * @return import
     */
    public String getSimpleTypeImport() {
        String fullyQualifiedType = getFullyQualifiedType();
        String fullyQualfiedImport = PythonElementUtils.derivePythonImport(fullyQualifiedType);

        return StringUtils.isNotBlank(fullyQualfiedImport) ? fullyQualfiedImport : null;
    }

    /**
     * Returns the import for this dictionary type's class, if generated.
     * 
     * @return generated class import
     */
    public String getGeneratedClassImport() {
        String generatedClassImport = null;
        if (isComplex()) {
            // modifiable python dictionary types are generated under src/${packageFolderName}/dictionary/
            // so using a relative import from a generated unmodifiable module, the type
            // name is: ...dictionary.dictionary_type_name.DictionaryTypeName
            String pythonPackage = "...dictionary." + getSnakeCaseName();
            String generatedClassType = pythonPackage + "." + getCapitalizedName();
            generatedClassImport = PythonElementUtils.derivePythonImport(generatedClassType);
        }

        return generatedClassImport;
    }

    /**
     * Returns the validation formats as comma-separated strings.
     * 
     * @return comma-separated format strings
     */
    public String getCommaSeparatedFormats() {
        StringJoiner stringJoiner = new StringJoiner(", ");

        if (hasFormatValidation()) {
            for (String format : getValidation().getFormats()) {
                stringJoiner.add("'" + format + "'");
            }
        }

        return stringJoiner.toString();
    }

    /**
     * Whether this dictionary type is a string type.
     * 
     * @return true if this dictionary type is a string type
     */
    public boolean isString() {
        return "str".equals(getShortType());
    }

    /**
     * Whether this dictionary type is a decimal type.
     * 
     * @return true if this dictionary type is a decimal type
     */
    public boolean isDecimal() {
        return "Decimal".equals(getShortType()) || "float".equals(getShortType());
    }

}
