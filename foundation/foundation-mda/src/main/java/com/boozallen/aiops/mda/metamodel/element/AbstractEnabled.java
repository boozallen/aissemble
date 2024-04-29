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

/**
 * Defines the contract for a generic element that can be enabled or disabled.
 */
public interface AbstractEnabled {

    /**
     * Returns true if the element is enabled.
     * 
     * @return true if the element is enabled
     */
    Boolean isEnabled();

}
