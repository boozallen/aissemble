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

import com.boozallen.aiops.mda.generator.common.FrameworkEnum;

/**
 * Defines the contract for how frameworks is configured.
 */
public interface Framework {

    /**
     * Returns list of frameworks to support.
     * @return the list of supported frameworks
     */
    FrameworkEnum getName();
}
