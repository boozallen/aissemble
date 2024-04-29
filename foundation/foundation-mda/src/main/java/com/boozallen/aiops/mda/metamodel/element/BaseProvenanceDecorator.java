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

import org.technologybrewery.fermenter.mda.metamodel.element.MetamodelUtils;

/**
 * Provides baseline decorator functionality for {@link Persist}.
 *
 * The goal is to make it easier to apply the decorator pattern in various
 * implementations of generators (e.g., Java, python, Docker) so that each
 * concrete decorator only has to decorate those aspects of the class that are
 * needed, not all the pass-through methods that each decorator would otherwise
 * need to implement (that add no real value).
 */
public class BaseProvenanceDecorator implements Provenance {

    protected Provenance wrapped;

    /**
     * New decorator for {@link Provenance}.
     *
     * @param provenanceToDecorate instance to decorate
     */
    public BaseProvenanceDecorator(Provenance provenanceToDecorate) {
        MetamodelUtils.validateWrappedInstanceIsNonNull(getClass(), provenanceToDecorate);
        wrapped = provenanceToDecorate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isEnabled() {
        return wrapped.isEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSubject() {
        return wrapped.getSubject();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResource() {
        return wrapped.getResource();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAction() {
        return wrapped.getAction();
    }
}
