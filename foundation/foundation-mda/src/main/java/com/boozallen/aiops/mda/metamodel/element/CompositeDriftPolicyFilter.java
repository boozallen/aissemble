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
 * Jackson filter to only write a field-level drift policy value if the field-level value is different than the
 * underlying type.
 */
public class CompositeDriftPolicyFilter {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CompositeFieldElement)) {
            return false;
        }

        CompositeFieldElement field = (CompositeFieldElement) obj;
        return field.driftPolicyOverrideExists();
    }

}
