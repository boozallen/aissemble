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
import org.technologybrewery.fermenter.mda.metamodel.element.NamespacedMetamodelElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Represents a dictionary type instance.
 */
@JsonPropertyOrder({ "name", "package", "simpleType", "validation", "protectionPolicy", "ethicsPolicy", "driftPolicy" })
public class DictionaryTypeElement extends NamespacedMetamodelElement implements DictionaryType {

    private String simpleType;

    @JsonInclude(Include.NON_NULL)
    private Validation validation;

    @JsonInclude(Include.NON_NULL)
    private String protectionPolicy;

    @JsonInclude(Include.NON_NULL)
    private String ethicsPolicy;

    @JsonInclude(Include.NON_NULL)
    private String driftPolicy;

    @JsonInclude(Include.NON_NULL)
    private String securityPolicy;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSimpleType() {
        return simpleType;
    }

    /**
     * {@inheritDoc}
     */
    @JsonInclude(Include.NON_NULL)
    @Override
    public Validation getValidation() {
        return validation;
    }

    /**
     * {@inheritDoc}
     */
    @JsonInclude(Include.NON_NULL)
    @Override
    public String getProtectionPolicy() {
        return protectionPolicy;
    }

    /**
     * {@inheritDoc}
     */
    @JsonInclude(Include.NON_NULL)
    @Override
    public String getEthicsPolicy() {
        return ethicsPolicy;
    }

    /**
     * {@inheritDoc}
     */
    @JsonInclude(Include.NON_NULL)
    @Override
    public String getDriftPolicy() {
        return driftPolicy;
    }

    /**
     * {@inheritDoc}
     */
    @JsonInclude(Include.NON_NULL)
    @Override
    public String getSecurityPolicy() {
        return securityPolicy;
    }

    /**
     * Sets the simple type for this dictionary type.
     * 
     * @param simpleType
     *            simple or primitive type
     */
    public void setSimpleType(String simpleType) {
        this.simpleType = simpleType;
    }

    /**
     * Sets the validation for this dictionary type.
     * 
     * @param validation
     *            validation constraints for this dictionary type
     */
    public void setValidation(Validation validation) {
        this.validation = validation;
    }

    /**
     * Sets the protection policy URN for this dictionary type.
     * 
     * @param protectionPolicy
     *            protection policy URN
     */
    public void setProtectionPolicy(String protectionPolicy) {
        this.protectionPolicy = protectionPolicy;
    }

    /**
     * Sets the ethics policy URN for this dictionary type.
     * 
     * @param ethicsPolicy
     *            ethics policy URN
     */
    public void setEthicsPolicy(String ethicsPolicy) {
        this.ethicsPolicy = ethicsPolicy;
    }

    /**
     * Sets the drift policy URN for this dictionary type.
     * 
     * @param driftPolicy
     *            drift policy URN
     */
    public void setDriftPolicy(String driftPolicy) {
        this.driftPolicy = driftPolicy;
    }

    /**
     * Sets the security policy URN for this dictionary type.
     *
     * @param securityPolicy
     *            security policy URN
     */
    public void setSecurityPolicy(String securityPolicy) {
        this.securityPolicy = securityPolicy;
    }

    @Override
    public void validate() {
        super.validate();

        if (StringUtils.isBlank(getSimpleType())) {
            messageTracker.addErrorMessage("A dictionary type has been specified without a required 'simpleType'!");

        }

        validateProtectionPolicy();
        validateEthicsPolicy();
        validateDriftPolicy();

        if (validation != null) {
            validation.validate();
        }

    }

    private void validateProtectionPolicy() {
        if (protectionPolicy != null && StringUtils.isBlank(protectionPolicy)) {
            protectionPolicy = null;
            addEmptyPolicyUrnMessage("protectionPolicy");

        }
    }

    private void addEmptyPolicyUrnMessage(String fieldName) {
        messageTracker.addWarningMessage(
                "Empty " + fieldName + " found and ignored - please remove from your DictionaryType!");
    }

    private void validateEthicsPolicy() {
        if (ethicsPolicy != null && StringUtils.isBlank(ethicsPolicy)) {
            ethicsPolicy = null;
            addEmptyPolicyUrnMessage("ethicsPolicy");

        }
    }

    private void validateDriftPolicy() {
        if (driftPolicy != null && StringUtils.isBlank(driftPolicy)) {
            driftPolicy = null;
            addEmptyPolicyUrnMessage("driftPolicy");

        }
    }

    /**
     * Part of the other schemas.
     * 
     * {@inheritDoc}
     */
    @Override
    public String getSchemaFileName() {
        return null;
    }
}
