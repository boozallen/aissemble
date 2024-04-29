package com.boozallen.aiops.mda.metamodel;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.technologybrewery.fermenter.mda.metamodel.AbstractMetamodelManager;

import com.boozallen.aiops.mda.metamodel.element.Composite;
import com.boozallen.aiops.mda.metamodel.element.CompositeElement;

/**
 * Responsible for maintaining the list of composite model instances elements in the system.
 */
class CompositeModelInstanceManager extends AbstractMetamodelManager<Composite> {

    private static final CompositeModelInstanceManager instance = new CompositeModelInstanceManager();

    /**
     * Returns the singleton instance of this class.
     * 
     * @return singleton
     */
    public static CompositeModelInstanceManager getInstance() {
        return instance;
    }

    /**
     * Prevent instantiation of this singleton from outside this class.
     */
    private CompositeModelInstanceManager() {
        super();
    }

    @Override
    protected String getMetadataLocation() {
        return "composites";
    }

    @Override
    protected Class<CompositeElement> getMetamodelClass() {
        return CompositeElement.class;
    }

    @Override
    protected String getMetamodelDescription() {
        return Composite.class.getSimpleName();
    }

}
