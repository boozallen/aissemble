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

import com.boozallen.aiops.mda.metamodel.AIOpsModelInstanceRepostory;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceRepositoryManager;
import org.technologybrewery.fermenter.mda.metamodel.element.NamespacedMetamodelElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Represents a step data record type instance.
 */
@JsonPropertyOrder({ "name", "package" })
public class StepDataRecordTypeElement extends NamespacedMetamodelElement implements StepDataRecordType {

    private AIOpsModelInstanceRepostory modelRepository = ModelInstanceRepositoryManager
            .getMetamodelRepository(AIOpsModelInstanceRepostory.class);

    /**
     * {@inheritDoc}
     */
    @JsonIgnore
    @Override
    public Record getRecordType() {
        Record record;
        if (StringUtils.isNotBlank(getPackage())) {
            record = modelRepository.getRecord(getPackage(), getName());
        } else {
            record = modelRepository.getRecord(getName());
        }

        return record;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        super.validate();

        if (getRecordType() == null) {
            messageTracker.addErrorMessage("Invalid record type - no record found! (package:'" + getPackage()
                    + "', name:'" + getName() + "')");
        }
    }

    /**
     * Part of the pipeline schema.
     * 
     * {@inheritDoc}
     */
    @Override
    public String getSchemaFileName() {
        return null;
    }

}
