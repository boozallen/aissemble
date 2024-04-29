package com.boozallen.aiops.mda.metamodel.element;

import org.apache.commons.lang3.StringUtils;

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

import com.boozallen.aiops.mda.generator.common.PersistMode;

/**
 * Provides baseline decorator functionality for {@link Persist}.
 * 
 * The goal is to make it easier to apply the decorator pattern in various implementations of generators (e.g., Java,
 * python, Docker) so that each concrete decorator only has to decorate those aspects of the class that are needed, not
 * all the pass-through methods that each decorator would otherwise need to implement (that add no real value).
 */
public class BasePersistDecorator implements Persist {

    protected Persist wrapped;

    /**
     * New decorator for {@link Persist}.
     * 
     * @param persistToDecorate
     *            instance to decorate
     */
    public BasePersistDecorator(Persist persistToDecorate) {
        MetamodelUtils.validateWrappedInstanceIsNonNull(getClass(), persistToDecorate);
        wrapped = persistToDecorate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        wrapped.validate();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return wrapped.getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMode() {
        String mode = wrapped.getMode();

        if (StringUtils.isBlank(mode)) {
            mode = PersistMode.APPEND.getModeType();
        }

        return mode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StepDataCollectionType getCollectionType() {
        return wrapped.getCollectionType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StepDataRecordType getRecordType() {
        return wrapped.getRecordType();
    }

}
