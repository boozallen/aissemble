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
import com.boozallen.aiops.mda.metamodel.element.Relation;
import com.boozallen.aiops.mda.metamodel.element.RelationElement;
import org.technologybrewery.fermenter.mda.generator.GenerationException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

    /**
     * Iterate over loaded domains and register each relation on its parent. This enables bi-directional referencing of
     * relations. It is also important to note that while referring to a parent from a child is very similar to a
     * reference, it is typically not similar enough to assume that it will be implemented in this fashion. Separating
     * them into their own collection ensures that they can be dealt with as appropriate for the target implementation.
     */
    protected void postLoadMetamodel() {

        super.postLoadMetamodel();

        RecordElement record;
        RecordElement childRecord;
        RelationElement relation;
        String relationType;
        List<Relation> relationMap;
        Iterator<Record> recordMapIterator;
        Iterator<Relation> relationValueInterator;

        // Get the complete metadata map - if I get only get current application, client transfer objects does not get
        // generated with parent references
        Map<String, Record> recordMap = getTargetMetadataMap();
        recordMapIterator = recordMap.values().iterator();
        while (recordMapIterator.hasNext()) {
            record = (RecordElement) recordMapIterator.next();
            relationMap = record.getRelations();

            relationValueInterator = (relationMap != null) ? relationMap.iterator() : Collections.emptyIterator();
            while (relationValueInterator.hasNext()) {
                relation = (RelationElement) relationValueInterator.next();
                relationType = relation.getName();
                // TODO: check 1-M and 1-1 only:
                childRecord = (RecordElement) recordMap.get(relationType);
                if (childRecord != null) {
                    childRecord.addInverseRelation(record);
                } else {
                    throw new GenerationException("Could not find a relation to record: " + relationType);
                }
            }

        }
    }

    private Map<String, Record> getTargetMetadataMap() {
        Map<String, Record> entityMap = new HashMap<>();

        List<String> targetedArtifactIds = repoConfiguration.getTargetModelInstances();
        for (String artifactId : targetedArtifactIds) {
            Map<String, Record> targetedModelMap = getMetadataByArtifactIdMap(artifactId);
            if (targetedModelMap != null) {
                entityMap.putAll(targetedModelMap);
            }
        }
        return entityMap;
    }

}
