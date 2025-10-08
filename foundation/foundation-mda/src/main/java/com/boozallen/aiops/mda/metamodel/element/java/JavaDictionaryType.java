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

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeSet;

import org.technologybrewery.fermenter.mda.TypeManager;

import com.boozallen.aiops.mda.metamodel.element.BaseDictionaryTypeDecorator;
import com.boozallen.aiops.mda.metamodel.element.DictionaryType;
import com.boozallen.aiops.mda.metamodel.element.util.JavaElementUtils;

/**
 * Decorates DictionaryType with Java-specific functionality.
 */
public class JavaDictionaryType extends BaseDictionaryTypeDecorator {

    private static final List<String> VALIDATABLE_TYPES = Arrays.asList("BigDecimal", "Double", "Float", "Integer",
            "Long", "Short", "String");

    private Set<String> imports = new TreeSet<>();

    /**
     * {@inheritDoc}
     */
    public JavaDictionaryType(DictionaryType dictionaryTypeToDecorate) {
        super(dictionaryTypeToDecorate);
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
     * Returns the base imports for this dictionary type.
     * 
     * @return base imports
     */
    public Set<String> getBaseImports() {
        addDictionaryTypeImport();

        if (hasValidationConstraints()) {
            addValidationImports();
        }

        return imports;
    }

    /**
     * Returns the implementation imports for this dictionary type.
     * 
     * @return implementation imports
     */
    public Set<String> getImplImports() {
        addDictionaryTypeImport();

        return imports;
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
                String escapedFormat = format.replace("\\", "\\\\");
                stringJoiner.add("\"" + escapedFormat + "\"");
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
        return "String".equals(getShortType());
    }

    /**
     * Whether this dictionary type is a decimal type.
     * 
     * @return true if this dictionary type is a decimal type
     */
    public boolean isDecimal() {
        return "BigDecimal".equals(getShortType()) || "Double".equals(getShortType()) || "Float".equals(getShortType());
    }

    private void addDictionaryTypeImport() {
        String fullyQualifiedType = getFullyQualifiedType();
        if (JavaElementUtils.isImportNeeded(fullyQualifiedType)) {
            imports.add(fullyQualifiedType);
        }
    }

    private void addValidationImports() {
        imports.add(JavaElementUtils.VALIDATION_EXCEPTION_IMPORT);

        if (isString()) {
            imports.add(JavaElementUtils.STRING_UTILS_IMPORT);
        }

        if (hasFormatValidation()) {
            imports.add(JavaElementUtils.ARRAYS_IMPORT);
            imports.add(TypeManager.getFullyQualifiedType("list"));
        }

        if (hasScaleValidation()) {
            imports.add(JavaElementUtils.ROUNDING_MODE_IMPORT);
            imports.add(TypeManager.getFullyQualifiedType("decimal"));
        }
    }

}
