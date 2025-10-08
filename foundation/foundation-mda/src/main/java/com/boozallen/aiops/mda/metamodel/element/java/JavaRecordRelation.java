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

import com.boozallen.aiops.mda.metamodel.element.BaseRecordRelationDecorator;
import com.boozallen.aiops.mda.metamodel.element.Relation;

import java.util.HashSet;
import java.util.Set;

import static com.boozallen.aiops.mda.metamodel.element.util.JavaElementUtils.LIST_IMPORT;
import static com.boozallen.aiops.mda.metamodel.element.util.JavaElementUtils.VALIDATION_EXCEPTION_IMPORT;

/**
 * Decorates RecordRelation with Java-specific functionality.
 */
public class JavaRecordRelation extends BaseRecordRelationDecorator {

    /**
     * {@inheritDoc}
     */
    public JavaRecordRelation(Relation relationToDecorate) {
        super(relationToDecorate);
    }

    /**
     * Returns the import for the generating the setters/getters of the reference record.
     * @return generated class import
     */
    public Set<String> getGeneratedClassImport() {
        Set<String> relationImports = new HashSet<>();
        if (isOneToManyRelation())
           relationImports.add(LIST_IMPORT);
        if (!this.isNullable()) {
            relationImports.add(VALIDATION_EXCEPTION_IMPORT);
        }
        return relationImports;
    }

    /**
     * Returns the reference record java setter functoin signature for generating the base record class
     * @return the reference record java setter function signature
     */
    public String getRelationSetterSignature() {
        String className = getName();
        if (this.isOneToManyRelation()) {
            return String.format("public void set%s(List<%s> %s)", className, className, this.getUncapitalizedName());
        } else {
            return String.format("public void set%s(%s %s)", className, className, getUncapitalizedName());
        }
    }

    /**
     * Returns the reference record java getter function signature for generating the base record class
     * @return the reference record java getter function signature
     */
    public String getRelationGetterSignature() {
        String className = getName();
        if (this.isOneToManyRelation()) {
            return String.format("public List<%s> get%s()", className, className);
        } else {
            return String.format("public %s get%s()", className, className);
        }
    }

    /**
     * Returns the reference record property declaration for generating the base record class
     * @return the reference record property declaration
     */
    public String getRelationPropDeclaration() {
        String type = String.format(isOneToManyRelation()? "List<%s>": "%s", getName());
        return String.format("private %s %s = null;", type, getUncapitalizedName());
    }

    /**
     * Returns the reference record validation logic for generating the base record class
     * @return the reference record validation logic
     */
    public String getRelationValidate() {
        if (this.isOneToManyRelation()) {
            String validate = """
                    for (%s record : this.%s) {
                        record.validate();
                    }""";
            return String.format(validate, getName(), getUncapitalizedName());
        } else {
            return String.format("this.%s.validate();",getUncapitalizedName());
        }
    }

    /**
     * Returns the required reference record validation logic for generating the base record class
     * @return the required reference record validation logic
     */
    public String getRequiredRelationValidate() {
        String uncapitalizedName = getUncapitalizedName();
        if (this.isOneToManyRelation()) {
            String validate = """
                    if (this.%s == null || this.%s.size() == 0) {
                        throw new ValidationException("Relation record '%s' is required");
                    } else {
                        %s
                    }""";
            return String.format(validate, uncapitalizedName, uncapitalizedName, getName(), getRelationValidate());
        } else {
            String validate = """
                    if (this.%s == null) {
                        throw new ValidationException("Relation record '%s' is required");
                    } else {
                        %s
                    }""";
            return String.format(validate, uncapitalizedName, getName(), getRelationValidate());
        }
    }

}
