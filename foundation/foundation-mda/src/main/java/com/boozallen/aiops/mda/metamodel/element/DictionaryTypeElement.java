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
import org.technologybrewery.fermenter.mda.metamodel.element.NamespacedMetamodelElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Represents a dictionary type instance.
 */
@JsonPropertyOrder({ "name", "package", "simpleType", "validation", "ethicsPolicy", "driftPolicy" })
public class DictionaryTypeElement extends NamespacedMetamodelElement implements DictionaryType {

    private String simpleType;

    @JsonInclude(Include.NON_NULL)
    private Validation validation;

    @JsonInclude(Include.NON_NULL)
    private String ethicsPolicy;

    @JsonInclude(Include.NON_NULL)
    private String driftPolicy;

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

    @Override
    public void validate() {
        super.validate();

        if (StringUtils.isBlank(getSimpleType())) {
            messageTracker.addErrorMessage("A dictionary type has been specified without a required 'simpleType'!");

        }

        validateEthicsPolicy();
        validateDriftPolicy();

        if (validation != null) {
            validation.validate();
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
