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
 * Provides baseline decorator functionality for {@link StepDataBinding}.
 * 
 * The goal is to make it easier to apply the decorator pattern in various implementations of generators (e.g., Java,
 * python, Docker) so that each concrete decorator only has to decorate those aspects of the class that are needed, not
 * all the pass-through methods that each decorator would otherwise need to implement (that add no real value).
 */
public class BaseStepDataBindingDecorator implements StepDataBinding {

    protected StepDataBinding wrapped;

    /**
     * New decorator for {@link StepDataBinding}.
     * 
     * @param stepDataBindingToDecorate
     *            instance to decorate
     */
    public BaseStepDataBindingDecorator(StepDataBinding stepDataBindingToDecorate) {
        MetamodelUtils.validateWrappedInstanceIsNonNull(getClass(), stepDataBindingToDecorate);
        wrapped = stepDataBindingToDecorate;
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
    public StepDataCollectionType getNativeCollectionType() {
        return wrapped.getNativeCollectionType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StepDataRecordType getRecordType() {
        return wrapped.getRecordType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getChannelType() {
        return wrapped.getChannelType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getChannelName() {
        return wrapped.getChannelName();
    }

}
