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

import com.boozallen.aiops.mda.generator.util.PipelineUtils;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.generator.GenerationException;
import org.technologybrewery.fermenter.mda.metamodel.element.MetamodelUtils;

/**
 * Provides baseline decorator functionality for {@link RecordField}.
 *
 * The goal is to make it easier to apply the decorator pattern in various implementations of generators (e.g., Java,
 * python, Docker) so that each concrete decorator only has to decorate those aspects of the class that are needed, not
 * all the pass-through methods that each decorator would otherwise need to implement (that add no real value).
 */
public class BaseRecordFieldDecorator implements RecordField {

    protected RecordField wrapped;

    /**
     * New decorator for {@link RecordField}.
     *
     * @param recordFieldToDecorate
     *            instance to decorate
     */
    public BaseRecordFieldDecorator(RecordField recordFieldToDecorate) {
        MetamodelUtils.validateWrappedInstanceIsNonNull(getClass(), recordFieldToDecorate);
        wrapped = recordFieldToDecorate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return wrapped.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getColumn() {
        return wrapped.getColumn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isRequired() {
        return wrapped.isRequired() != null && wrapped.isRequired();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProtectionPolicy() {
        return wrapped.getProtectionPolicy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEthicsPolicy() {
        return wrapped.getEthicsPolicy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDriftPolicy() {
        return wrapped.getDriftPolicy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSecurityPolicy() {
        return wrapped.getSecurityPolicy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        wrapped.validate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecordFieldType getType() {
        return wrapped.getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return wrapped.getDescription();
    }

    /**
     * Returns the field name, capitalized.
     *
     * @return capitalized name
     */
    public String getCapitalizedName() {
        return StringUtils.capitalize(getName());
    }

    /**
     * Returns the field name formatted to uppercase with underscores.
     *
     * @return name formatted to uppercase with underscores
     */
    public String getUpperSnakecaseName() {
        return PipelineUtils.deriveUpperUnderscoreNameFromUpperCamelName(getName());
    }

    /**
     * Returns the short implementation of this field's type.
     *
     * @return short implementation
     */
    public String getShortType() {
        String shortType = null;
        RecordFieldType fieldType = getType();

        if (fieldType.isDictionaryTyped()) {
            BaseDictionaryTypeDecorator dictionaryType = (BaseDictionaryTypeDecorator) fieldType.getDictionaryType();
            shortType = dictionaryType.isComplex() ? dictionaryType.getCapitalizedName()
                    : dictionaryType.getShortType();
        } else if (fieldType.isCompositeTyped()) {
            throw new GenerationException("Composite typed field not supported yet!");
        }

        return shortType;
    }

    /**
     * Whether this field has validation.
     *
     * @return true if this field has validation
     */
    public boolean hasValidation() {
        boolean hasValidation = false;
        RecordFieldType fieldType = getType();

        if (fieldType.isDictionaryTyped()) {
            BaseDictionaryTypeDecorator dictionaryType = (BaseDictionaryTypeDecorator) fieldType.getDictionaryType();
            hasValidation = dictionaryType.hasValidationConstraints();
        }

        return hasValidation;
    }

    /**
     * Get the validation associated with the field
     *
     * @return validations associated with the field
     */
    public Validation getValidation() {
        RecordFieldType fieldType = getType();
        if (hasValidation()) {
            BaseDictionaryTypeDecorator dictionaryType = (BaseDictionaryTypeDecorator) fieldType.getDictionaryType();
            return dictionaryType.getValidation();
        }
        return null;
    }

    /**
     * Returns the drift policy value for the field's enum.
     *
     * @return drift policy value for the field's enum
     */
    public String getDriftPolicyEnumValue() {
        String driftPolicyValue;
        if (StringUtils.isNotBlank(getDriftPolicy())) {
            if (hasOverriddenDriftPolicy()) {
                driftPolicyValue = getQuotationString() + getDriftPolicy() + getQuotationString();
            } else {
                // use dictionary type's DRIFT_POLICY constant
                BaseDictionaryTypeDecorator dictionaryType = (BaseDictionaryTypeDecorator) getType().getDictionaryType();
                driftPolicyValue = dictionaryType.getCapitalizedName() + ".DRIFT_POLICY";
            }
        } else {
            driftPolicyValue = getNullString();
        }

        return driftPolicyValue;
    }

    /**
     * Returns the ethics policy value for the field's enum.
     *
     * @return ethics policy value for the field's enum
     */
    public String getEthicsPolicyEnumValue() {
        String ethicsPolicyValue;
        if (StringUtils.isNotBlank(getEthicsPolicy())) {
            if (hasOverriddenEthicsPolicy()) {
                ethicsPolicyValue = getQuotationString() + getEthicsPolicy() + getQuotationString();
            } else {
                // use dictionary type's ETHICS_POLICY constant
                BaseDictionaryTypeDecorator dictionaryType = (BaseDictionaryTypeDecorator) getType().getDictionaryType();
                ethicsPolicyValue = dictionaryType.getCapitalizedName() + ".ETHICS_POLICY";
            }
        } else {
            ethicsPolicyValue = getNullString();
        }

        return ethicsPolicyValue;
    }

    /**
     * Returns the protection policy value for the field's enum.
     *
     * @return protection policy value for the field's enum
     */
    public String getProtectionPolicyEnumValue() {
        String protectionPolicyValue;
        if (StringUtils.isNotBlank(getProtectionPolicy())) {
            if (hasOverriddenProtectionPolicy()) {
                protectionPolicyValue = getQuotationString() + getProtectionPolicy() + getQuotationString();
            } else {
                // use dictionary type's PROTECTION_POLICY constant
                BaseDictionaryTypeDecorator dictionaryType = (BaseDictionaryTypeDecorator) getType().getDictionaryType();
                protectionPolicyValue = dictionaryType.getCapitalizedName() + ".PROTECTION_POLICY";
            }
        } else {
            protectionPolicyValue = getNullString();
        }

        return protectionPolicyValue;
    }

    protected String getQuotationString() {
        return "\"";
    }

    protected String getNullString() {
        return "null";
    }

    /**
     * Whether this field overrides a dictionary type drift policy.
     *
     * @return true if this field overrides a dictionary type drift policy
     */
    public boolean hasOverriddenDriftPolicy() {
        String baseDriftPolicy = null;
        RecordFieldType fieldType = getType();
        if (fieldType.isDictionaryTyped()) {
            baseDriftPolicy = fieldType.getDictionaryType().getDriftPolicy();
        }

        return StringUtils.isNotBlank(getDriftPolicy()) && !getDriftPolicy().equals(baseDriftPolicy);
    }

    /**
     * Whether this field overrides a dictionary type ethics policy.
     *
     * @return true if this field overrides a dictionary type ethics policy
     */
    public boolean hasOverriddenEthicsPolicy() {
        String baseEthicsPolicy = null;
        RecordFieldType fieldType = getType();
        if (fieldType.isDictionaryTyped()) {
            baseEthicsPolicy = fieldType.getDictionaryType().getEthicsPolicy();
        }

        return StringUtils.isNotBlank(getEthicsPolicy()) && !getEthicsPolicy().equals(baseEthicsPolicy);
    }

    /**
     * Whether this field overrides a dictionary type protection policy.
     *
     * @return true if this field overrides a dictionary type protection policy
     */
    public boolean hasOverriddenProtectionPolicy() {
        String baseProtectionPolicy = null;
        RecordFieldType fieldType = getType();
        if (fieldType.isDictionaryTyped()) {
            baseProtectionPolicy = fieldType.getDictionaryType().getProtectionPolicy();
        }

        return StringUtils.isNotBlank(getProtectionPolicy()) && !getProtectionPolicy().equals(baseProtectionPolicy);
    }

    /**
     * Returns this field's column value as the field name if provided,
     * otherwise returns this field's name value.
     *
     * @return field name
     */
    public String getFieldName() {
        String fieldName;
        if (!StringUtils.isBlank(getColumn())) {
            fieldName = getColumn();
        } else {
            fieldName = getName();
        }

        return fieldName;
    }
}

class SchemaField {
    private String name;
    private String description;
    private String type;
    private String column;
    private boolean required;

    public SchemaField(BaseRecordFieldDecorator baseField) {
        this.name = baseField.getName();
        this.description = baseField.getDescription();
        this.type = baseField.getType().getName();
        this.column = baseField.getColumn();
        this.required = baseField.isRequired();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
