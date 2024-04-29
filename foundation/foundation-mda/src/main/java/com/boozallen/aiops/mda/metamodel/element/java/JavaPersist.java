package com.boozallen.aiops.mda.metamodel.element.java;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.TypeManager;

import com.boozallen.aiops.mda.metamodel.element.BasePersistDecorator;
import com.boozallen.aiops.mda.metamodel.element.Persist;
import com.boozallen.aiops.mda.metamodel.element.StepDataCollectionType;
import com.boozallen.aiops.mda.metamodel.element.StepDataRecordType;

/**
 * Decorates Persist with Java-specific functionality.
 */
public class JavaPersist extends BasePersistDecorator {

    private static final String ROW = "row";
    private static final String DATASET = "dataset";

    /**
     * {@inheritDoc}
     */
    public JavaPersist(Persist persistToDecorate) {
        super(persistToDecorate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StepDataCollectionType getCollectionType() {
        return super.getCollectionType() != null ? new JavaStepDataCollectionType(super.getCollectionType()) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StepDataRecordType getRecordType() {
        return super.getRecordType() != null ? new JavaStepDataRecordType(super.getRecordType()) : null;
    }

    /**
     * Returns the fully qualified collection type.
     * 
     * @return fully qualified collection type
     */
    public String getFullyQualifiedCollectionType() {
        JavaStepDataCollectionType collectionType = (JavaStepDataCollectionType) getCollectionType();
        return collectionType != null ? collectionType.getFullyQualifiedType() : TypeManager.getFullyQualifiedType(DATASET);
    }

    /**
     * Returns the short collection type.
     * 
     * @return short collection type
     */
    public String getShortCollectionType() {
        JavaStepDataCollectionType collectionType = (JavaStepDataCollectionType) getCollectionType();
        return collectionType != null ? collectionType.getShortType() : TypeManager.getShortType(DATASET);
    }

    /**
     * Returns the fully qualified record type.
     * 
     * @return fully qualified record type
     */
    public String getFullyQualifiedRecordType() {
        JavaStepDataRecordType recordType = (JavaStepDataRecordType) getRecordType();
        return recordType != null ? recordType.getFullyQualifiedType() : TypeManager.getFullyQualifiedType(ROW);
    }

    /**
     * Returns the short record type.
     * 
     * @return short record type
     */
    public String getShortRecordType() {
        JavaStepDataRecordType recordType = (JavaStepDataRecordType) getRecordType();
        return recordType != null ? recordType.getName() : TypeManager.getShortType(ROW);
    }

    /**
     * Returns import values needed by this Java persist instance.
     * 
     * @return imports
     */
    public Set<String> getImports() {
        Set<String> imports = new HashSet<>();
        imports.add(getFullyQualifiedCollectionType());

        String fullyQualifiedRecordType = getFullyQualifiedRecordType();
        if (StringUtils.isNotBlank(fullyQualifiedRecordType)) {
            imports.add(fullyQualifiedRecordType);
        }

        if (!"custom".equals(getType())) {
            imports.add(TypeManager.getFullyQualifiedType("savemode"));
        }

        return imports;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMode() {
        String mode = "savemode-" + super.getMode();
        return TypeManager.getShortType(mode);
    }

}
