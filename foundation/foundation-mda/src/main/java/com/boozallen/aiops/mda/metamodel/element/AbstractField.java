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

import org.technologybrewery.fermenter.mda.metamodel.element.Validatable;

/**
 * Defines the contract for a generic field.
 */
public interface AbstractField extends Validatable {

    /**
     * Returns the name of the field.
     * 
     * @return name
     */
    String getName();

    /**
     * Returns the column of the field.
     * 
     * @return column
     */
    String getColumn();

    /**
     * Returns whether or not the field is required.
     * 
     * @return required
     */
    Boolean isRequired();

    /**
     * Returns the protection policy for this type. This should be a URN to an applicable policy in the Secrets as a
     * Service module. This value will override a dictionary type setting, if appropriate.
     * 
     * @return protection policy urn
     */
    String getProtectionPolicy();

    /**
     * Returns the ethics policy for this type. This should be a URN to an applicable policy in the Ethics/Bias module.
     * This value will override a dictionary type setting, if appropriate.
     * 
     * @return ethics policy urn
     */
    String getEthicsPolicy();

    /**
     * Returns the drift policy for this type. This should be a URN to an applicable policy in the Drift module. This
     * value will override a dictionary type setting, if appropriate.
     * 
     * @return drift policy urn
     */
    String getDriftPolicy();

    /**
     * Returns the security policy for this type. This should be a URN to an applicable policy in the Security module.
     * This value will override a dictionary type setting, if appropriate.
     *
     * @return security policy urn
     */
    String getSecurityPolicy();

}
