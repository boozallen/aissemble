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

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.TypeManager;
import org.technologybrewery.fermenter.mda.generator.GenerationException;
import org.technologybrewery.fermenter.mda.metamodel.element.MetamodelUtils;

/**
 * Provides baseline decorator functionality for {@link DictionaryType}.
 * 
 * The goal is to make it easier to apply the decorator pattern in various implementations of generators (e.g., Java,
 * python, Docker) so that each concrete decorator only has to decorate those aspects of the class that are needed, not
 * all the pass-through methods that each decorator would otherwise need to implement (that add no real value).
 */
public class BaseDictionaryTypeDecorator implements DictionaryType {

    protected DictionaryType wrapped;

    /**
     * New decorator for {@link DictionaryType}.
     * 
     * @param dictionaryTypeToDecorate
     *            instance to decorate
     */
    public BaseDictionaryTypeDecorator(DictionaryType dictionaryTypeToDecorate) {
        MetamodelUtils.validateWrappedInstanceIsNonNull(getClass(), dictionaryTypeToDecorate);
        wrapped = dictionaryTypeToDecorate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPackage() {
        return wrapped.getPackage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFileName() {
        return wrapped.getFileName();
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
    public void validate() {
        wrapped.validate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSimpleType() {
        return wrapped.getSimpleType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Validation getValidation() {
        return wrapped.getValidation();
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

    protected void validateValidations(List<String> validatableTypes, boolean isString, boolean isDecimal) {
        if (hasValidationConstraints() && !validatableTypes.contains(getShortType())) {
            throw new GenerationException("Dictionary type '" + getName()
                    + "' has validation but is not a validatable type. Please remove the validation for this type.");
        }

        if (hasLengthValidation() && !isString) {
            throw new GenerationException("Dictionary type '" + getName()
                    + "' has length validation but is not a string type. Please remove any min/max length validation for this type.");
        }

        if (hasFormatValidation() && !isString) {
            throw new GenerationException("Dictionary type '" + getName()
                    + "' has format validation but is not a string type. Please remove the format validation for this type.");
        }

        if (hasValueValidation() && isString) {
            throw new GenerationException("Dictionary type '" + getName()
                    + "' has value validation but is not a numeric type. Please remove any min/max value validation for this type.");
        }

        if (hasScaleValidation() && !isDecimal) {
            throw new GenerationException("Dictionary type '" + getName()
                    + "' has scale validation but is not a decimal type. Please remove the scale validation for this type.");
        }
    }

    /**
     * Returns the capitalized name of this dictionary type.
     * 
     * @return capitalized name
     */
    public String getCapitalizedName() {
        return StringUtils.capitalize(getName());
    }

    /**
     * Returns the fully qualified implementation for the simple type.
     * 
     * @return fully qualified implementation for the simple type
     */
    public String getFullyQualifiedType() {
        return TypeManager.getFullyQualifiedType(getSimpleType());
    }

    /**
     * Returns the short implementation for the simple type.
     * 
     * @return short implementation for the simple type
     */
    public String getShortType() {
        return TypeManager.getShortType(getSimpleType());
    }

    /**
     * Whether this dictionary type has validation constraints.
     * 
     * @return true if this dictionary type has validation constraints
     */
    public boolean hasValidationConstraints() {
        ValidationElement validation = (ValidationElement) getValidation();
        return validation != null && validation.hasValidationContraints();
    }

    /**
     * Whether this dictionary type is complex (e.g., has more than just
     * simpleType defined).
     * 
     * @return true if this dictionary type is complex
     */
    public boolean isComplex() {
        boolean hasDriftPolicy = StringUtils.isNotBlank(getDriftPolicy());
        boolean hasEthicsPolicy = StringUtils.isNotBlank(getEthicsPolicy());
        boolean hasProtectionPolicy = StringUtils.isNotBlank(getProtectionPolicy());

        return hasValidationConstraints() || hasDriftPolicy || hasEthicsPolicy || hasProtectionPolicy;
    }

    /**
     * Whether this dictionary type has length validation.
     * 
     * @return true if this dictionary type has length validation
     */
    public boolean hasLengthValidation() {
        Validation validation = getValidation();
        return validation != null && (validation.getMaxLength() != null || validation.getMinLength() != null);
    }

    /**
     * Whether this dictionary type has value validation.
     * 
     * @return true if this dictionary type has value validation
     */
    public boolean hasValueValidation() {
        Validation validation = getValidation();
        return validation != null && (validation.getMaxValue() != null || validation.getMinValue() != null);
    }

    /**
     * Whether this dictionary type has scale validation.
     * 
     * @return true if this dictionary type has scale validation
     */
    public boolean hasScaleValidation() {
        Validation validation = getValidation();
        return validation != null && validation.getScale() != null;
    }

    /**
     * Whether this dictionary type has format validation.
     * 
     * @return true if this dictionary type format length validation
     */
    public boolean hasFormatValidation() {
        Validation validation = getValidation();
        return validation != null && CollectionUtils.isNotEmpty(validation.getFormats());
    }

}
