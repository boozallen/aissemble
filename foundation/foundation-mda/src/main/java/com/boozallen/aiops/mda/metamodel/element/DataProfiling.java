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
 * Defines the contract for how data profiling is configured.
 */
public interface DataProfiling extends AbstractEnabled, Validatable {
    /**
     * Returns bill of material settings for this data access.
     * @return bill of materials.
     */
}
