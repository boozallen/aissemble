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
import org.technologybrewery.fermenter.mda.metamodel.element.MetamodelElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Represents a abstract field instance.
 */
public abstract class AbstractFieldElement extends MetamodelElement implements AbstractField {

    @JsonInclude(Include.NON_NULL)
    private String column;

    @JsonInclude(Include.NON_NULL)
    private Boolean required;

    @JsonInclude(Include.NON_NULL)
    private Validation validation;

    @JsonInclude(Include.NON_NULL)
    private String ethicsPolicy;

    @JsonInclude(Include.NON_NULL)
    private String driftPolicy;

    /**
     * {@inheritDoc}
     */
    @JsonInclude(Include.NON_NULL)
    @Override
    public String getColumn() {
        return column;
    }

    /**
     * {@inheritDoc}
     */
    @JsonInclude(Include.NON_NULL)
    @Override
    public Boolean isRequired() {
        return required;
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
     * Sets the simple type for this field.
     * 
     * @param column
     *            simple or primitive type
     */
    public void setColumn(String column) {
        this.column = column;
    }
    
    /**
     * Sets whether or not this field is required.
     * 
     * @param required
     *            boolean indicating requiredness
     */
    public void setRequired(Boolean required) {
        this.required = required;
    }

    /**
     * Sets the ethics policy URN for this field.
     * 
     * @param ethicsPolicy
     *            ethics policy URN
     */
    public void setEthicsPolicy(String ethicsPolicy) {
        this.ethicsPolicy = ethicsPolicy;
    }

    /**
     * Sets the drift policy URN for this field.
     * 
     * @param driftPolicy
     *            drift policy URN
     */
    public void setDriftPolicy(String driftPolicy) {
        this.driftPolicy = driftPolicy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        super.validate();

        validateEthicsPolicy();
        validateDriftPolicy();

        if (validation != null) {
            validation.validate();
        }

    }

    private void addEmptyPolicyUrnMessage(String fieldName) {
        messageTracker.addWarningMessage(
                "Empty " + fieldName + " found and ignored - please remove from your field!");
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
}
