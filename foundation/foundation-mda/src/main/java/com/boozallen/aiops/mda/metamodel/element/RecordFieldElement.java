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

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Represents a field with specific to records.
 */
@JsonPropertyOrder({ "name", "description", "type", "column", "required", "validation",
        "ethicsPolicy", "driftPolicy" })
public class RecordFieldElement extends AbstractFieldElement implements RecordField {

    @JsonInclude(Include.NON_NULL)
    private String description;

    @JsonInclude(Include.NON_NULL)
    private RecordFieldType type;

    /**
     * {@inheritDoc}
     */
    @JsonInclude(Include.NON_NULL)
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    @JsonInclude(Include.NON_NULL)
    @Override
    public RecordFieldType getType() {
        return type;
    }
    
    /**
     * Checks for a local override, then goes to the dictionary type, if appropriate, for the ethics policy value.
     * 
     * {@inheritDoc}
     */
    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = RecordEthicsPolicyFilter.class)
    @Override
    public String getEthicsPolicy() {
        String ethicsPolicy = super.getEthicsPolicy();
        if (StringUtils.isBlank(ethicsPolicy)) {
            RecordFieldType recordFieldType = getType();
            if (recordFieldType.isDictionaryTyped()) {
                DictionaryType dictionaryType = recordFieldType.getDictionaryType();
                ethicsPolicy = dictionaryType.getEthicsPolicy();
            }
        }
        return ethicsPolicy;
    }

    boolean ethicsPolicyOverrideExists() {
        String overidePolicy = super.getEthicsPolicy();
        String backingPolicy = type.isDictionaryTyped() ? type.getDictionaryType().getEthicsPolicy() : null;
        return StringUtils.compare(overidePolicy, backingPolicy) != 0;
    }   
    
    /**
     * Checks for a local override, then goes to the dictionary type, if appropriate, for the drift policy value.
     * 
     * {@inheritDoc}
     */
    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = RecordDriftPolicyFilter.class)
    @Override
    public String getDriftPolicy() {
        String driftPolicy = super.getDriftPolicy();
        if (StringUtils.isBlank(driftPolicy)) {
            RecordFieldType recordFieldType = getType();
            if (recordFieldType.isDictionaryTyped()) {
                DictionaryType dictionaryType = recordFieldType.getDictionaryType();
                driftPolicy = dictionaryType.getDriftPolicy();
            }
        }
        return driftPolicy;
    }

    boolean driftPolicyOverrideExists() {
        String overidePolicy = super.getDriftPolicy();
        String backingPolicy = type.isDictionaryTyped() ? type.getDictionaryType().getDriftPolicy() : null;
        return StringUtils.compare(overidePolicy, backingPolicy) != 0;
    }

    /**
     * Sets the type for this record field.
     * 
     * @param type
     *            type constraints for this field
     */
    public void setType(RecordFieldType type) {
        this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        super.validate();

        type.validate();

    }

    public void setDescription(String description) {
        this.description = description;
    }

}
