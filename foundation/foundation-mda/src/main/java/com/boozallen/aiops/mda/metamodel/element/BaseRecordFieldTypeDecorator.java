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
 * Provides baseline decorator functionality for {@link RecordFieldType}.
 * 
 * The goal is to make it easier to apply the decorator pattern in various implementations of generators (e.g., Java,
 * python, Docker) so that each concrete decorator only has to decorate those aspects of the class that are needed, not
 * all the pass-through methods that each decorator would otherwise need to implement (that add no real value).
 */
public class BaseRecordFieldTypeDecorator implements RecordFieldType {

    protected RecordFieldType wrapped;

    /**
     * New decorator for {@link RecordFieldType}.
     * 
     * @param recordFieldTypeToDecorate
     *            instance to decorate
     */
    public BaseRecordFieldTypeDecorator(RecordFieldType recordFieldTypeToDecorate) {
        MetamodelUtils.validateWrappedInstanceIsNonNull(getClass(), recordFieldTypeToDecorate);
        wrapped = recordFieldTypeToDecorate;
    }

    /**
     * New decorator for {@link RecordFieldType}.
     * 
     * @param recordToDecorate
     *            instance to decorate
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
    public boolean isDictionaryTyped() {
        return wrapped.isDictionaryTyped();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCompositeTyped() {
        return wrapped.isCompositeTyped();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DictionaryType getDictionaryType() {
        return wrapped.getDictionaryType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Composite getCompositeType() {
        return wrapped.getCompositeType();
    }

}
