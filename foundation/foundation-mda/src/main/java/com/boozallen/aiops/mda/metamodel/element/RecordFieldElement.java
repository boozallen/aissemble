package com.boozallen.aiops.mda.metamodel.element;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Represents a field with specific to records.
 */
@JsonPropertyOrder({ "name", "description", "type", "column", "required", "validation", "protectionPolicy",
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
     * Checks for a local override, then goes to the dictionary type, if appropriate, for the protection policy value.
     * 
     * {@inheritDoc}
     */
    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = RecordProtectionPolicyFilter.class)
    @Override
    public String getProtectionPolicy() {
        String protectionPolicy = super.getProtectionPolicy();
        if (StringUtils.isBlank(protectionPolicy)) {
            RecordFieldType recordFieldType = getType();
            if (recordFieldType.isDictionaryTyped()) {
                DictionaryType dictionaryType = recordFieldType.getDictionaryType();
                protectionPolicy = dictionaryType.getProtectionPolicy();
            }
        }
        return protectionPolicy;
    }

    boolean protectionPolicyOverrideExists() {
        String overidePolicy = super.getProtectionPolicy();
        String backingPolicy = type.isDictionaryTyped() ? type.getDictionaryType().getProtectionPolicy() : null;
        return StringUtils.compare(overidePolicy, backingPolicy) != 0;
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
     * Checks for a local override, then goes to the dictionary type, if appropriate, for the security policy value.
     *
     * {@inheritDoc}
     */
    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = RecordSecurityPolicyFilter.class)
    @Override
    public String getSecurityPolicy() {
        String securityPolicy = super.getSecurityPolicy();
        if (StringUtils.isBlank(securityPolicy)) {
            RecordFieldType recordFieldType = getType();
            if (recordFieldType.isDictionaryTyped()) {
                DictionaryType dictionaryType = recordFieldType.getDictionaryType();
                securityPolicy = dictionaryType.getSecurityPolicy();
            }
        }
        return securityPolicy;
    }

    boolean securityPolicyOverrideExists() {
        String overidePolicy = super.getSecurityPolicy();
        String backingPolicy = type.isDictionaryTyped() ? type.getDictionaryType().getSecurityPolicy() : null;
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
