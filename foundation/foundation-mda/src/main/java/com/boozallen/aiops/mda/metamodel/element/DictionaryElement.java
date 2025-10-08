package com.boozallen.aiops.mda.metamodel.element;

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

import org.technologybrewery.fermenter.mda.metamodel.element.NamespacedMetamodelElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Represents a dictionary instance.
 */
@JsonPropertyOrder({ "package", "name" })
public class DictionaryElement extends NamespacedMetamodelElement implements Dictionary {

    @JsonInclude(Include.NON_NULL)
    private List<DictionaryType> dictionaryTypes = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    @JsonInclude(Include.NON_NULL)
    @Override
    public List<DictionaryType> getDictionaryTypes() {
        return dictionaryTypes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSchemaFileName() {
        return "aiops-dictionary-schema.json";
    }

    /**
     * Adds a type to this dictionary.
     * 
     * @param dictionaryType
     *            type to add
     */
    public void addDictionaryType(DictionaryType dictionaryType) {
        this.dictionaryTypes.add(dictionaryType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        super.validate();

        for (DictionaryType dictionaryType : dictionaryTypes) {
            dictionaryType.validate();
        }

    }

}
