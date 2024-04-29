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

import org.technologybrewery.fermenter.mda.metamodel.element.NamespacedMetamodel;

/**
 * Defines the contract for an entry in the dictionary (e.g., validation, policies).
 */
public interface DictionaryType extends NamespacedMetamodel {

    /**
     * Returns the simple type of this dictionary type (e.g., string, integer).
     * 
     * @return simple type of dictionary type
     */
    String getSimpleType();

    /**
     * Returns any validation constraints associated with this type (e.g., length restrictions, regex).
     * 
     * @return validation constrains of the dictionary type
     */
    Validation getValidation();

    /**
     * Returns the protection policy for this type. This should be a URN to an applicable policy in the Secrets as a
     * Service module.
     * 
     * @return protection policy urn
     */
    String getProtectionPolicy();

    /**
     * Returns the ethics policy for this type. This should be a URN to an applicable policy in the Ethics/Bias module.
     * 
     * @return ethics policy urn
     */
    String getEthicsPolicy();

    /**
     * Returns the drift policy for this type. This should be a URN to an applicable policy in the Drift module.
     * 
     * @return drift policy urn
     */
    String getDriftPolicy();

    /**
     * Returns the security policy for this type. This should be a URN to an applicable policy in the encryption module.
     *
     * @return security policy urn
     */
    String getSecurityPolicy();

}
