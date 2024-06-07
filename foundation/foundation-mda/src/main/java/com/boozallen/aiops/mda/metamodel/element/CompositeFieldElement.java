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

import com.boozallen.aiops.mda.metamodel.AIOpsModelInstanceRepostory;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceRepositoryManager;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Represents a composite specific field.
 */
@JsonPropertyOrder({ "name", "description", "type", "column", "required", "validation", "protectionPolicy",
        "ethicsPolicy", "driftPolicy" })
public class CompositeFieldElement extends AbstractFieldElement implements CompositeField {

    private AIOpsModelInstanceRepostory modelRepository = ModelInstanceRepositoryManager
            .getMetamodelRepository(AIOpsModelInstanceRepostory.class);

    @JsonInclude(Include.NON_NULL)
    private String description;

    @JsonInclude(Include.NON_NULL)
    private DictionaryType type;

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
    public DictionaryType getType() {
        return type;
    }

    /**
     * Checks for a local override, then goes to the dictionary type for the protection policy value.
     * 
     * {@inheritDoc}
     */
    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = CompositeProtectionPolicyFilter.class)
    @Override
    public String getProtectionPolicy() {
        String protectionPolicy = null;
        protectionPolicy = super.getProtectionPolicy();
        if (StringUtils.isBlank(protectionPolicy)) {
            protectionPolicy = type.getProtectionPolicy();
        }
        return protectionPolicy;
    }

    boolean protectionPolicyOverrideExists() {
        String overidePolicy = super.getProtectionPolicy();
        String backingPolicy = type.getProtectionPolicy();
        return StringUtils.compare(overidePolicy, backingPolicy) != 0;
    }

    /**
     * Checks for a local override, then goes to the dictionary type for the ethics policy value.
     * 
     * {@inheritDoc}
     */
    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = CompositeEthicsPolicyFilter.class)
    @Override
    public String getEthicsPolicy() {
        String ethicsPolicy = null;
        ethicsPolicy = super.getEthicsPolicy();
        if (StringUtils.isBlank(ethicsPolicy)) {
            ethicsPolicy = type.getEthicsPolicy();
        }
        return ethicsPolicy;
    }

    boolean ethicsPolicyOverrideExists() {
        String overidePolicy = super.getEthicsPolicy();
        String backingPolicy = type.getEthicsPolicy();
        return StringUtils.compare(overidePolicy, backingPolicy) != 0;
    }

    /**
     * Checks for a local override, then goes to the dictionary type for the drift policy value.
     * 
     * {@inheritDoc}
     */
    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = CompositeDriftPolicyFilter.class)
    @Override
    public String getDriftPolicy() {
        String driftPolicy = null;
        driftPolicy = super.getDriftPolicy();
        if (StringUtils.isBlank(driftPolicy)) {
            driftPolicy = type.getDriftPolicy();
        }
        return driftPolicy;
    }

    boolean driftPolicyOverrideExists() {
        String overidePolicy = super.getDriftPolicy();
        String backingPolicy = type.getDriftPolicy();
        return StringUtils.compare(overidePolicy, backingPolicy) != 0;
    }

    /**
     * Sets the type for this dictionary field.
     * 
     * @param dictionaryType
     *            dictionary type constraints for this field
     */
    public void setType(DictionaryType type) {
        this.type = type;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        super.validate();

        DictionaryType foundType;
        if (StringUtils.isNotBlank(type.getPackage())) {
            foundType = modelRepository.getDictionaryType(type.getPackage(), type.getName());
        } else {
            foundType = modelRepository.getDictionaryType(type.getName());
        }

        type = foundType;

        type.validate();

    }

    public void setDescription(String description) {
        this.description = description;
    }

}
