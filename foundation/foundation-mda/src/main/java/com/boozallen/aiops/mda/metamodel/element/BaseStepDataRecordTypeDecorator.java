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
 * Provides baseline decorator functionality for {@link StepDataRecordType}.
 * 
 * The goal is to make it easier to apply the decorator pattern in various implementations of generators (e.g., Java,
 * python, Docker) so that each concrete decorator only has to decorate those aspects of the class that are needed, not
 * all the pass-through methods that each decorator would otherwise need to implement (that add no real value).
 */
public class BaseStepDataRecordTypeDecorator implements StepDataRecordType {

    protected StepDataRecordType wrapped;

    /**
     * New decorator for {@link StepDataRecordType}.
     * 
     * @param stepDataRecordTypeToDecorate
     *            instance to decorate
     */
    public BaseStepDataRecordTypeDecorator(StepDataRecordType stepDataRecordTypeToDecorate) {
        MetamodelUtils.validateWrappedInstanceIsNonNull(getClass(), stepDataRecordTypeToDecorate);
        wrapped = stepDataRecordTypeToDecorate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPackage() {
        return wrapped.getPackage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFileName() {
        return wrapped.getFileName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return wrapped.getName();
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
    public Record getRecordType() {
        return wrapped.getRecordType();
    }

}
