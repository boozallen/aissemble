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

import com.boozallen.aiops.mda.metamodel.element.Record;
import com.boozallen.aiops.mda.metamodel.element.RecordElement;

/**
 * Responsible for maintaining the list of record model instances elements in the system.
 */
class RecordModelInstanceManager extends AbstractMetamodelManager<Record> {

    private static final RecordModelInstanceManager instance = new RecordModelInstanceManager();

    /**
     * Returns the singleton instance of this class.
     * 
     * @return singleton
     */
    public static RecordModelInstanceManager getInstance() {
        return instance;
    }

    /**
     * Prevent instantiation of this singleton from outside this class.
     */
    private RecordModelInstanceManager() {
        super();
    }

    @Override
    protected String getMetadataLocation() {
        return "records";
    }

    @Override
    protected Class<RecordElement> getMetamodelClass() {
        return RecordElement.class;
    }

    @Override
    protected String getMetamodelDescription() {
        return Record.class.getSimpleName();
    }

}
