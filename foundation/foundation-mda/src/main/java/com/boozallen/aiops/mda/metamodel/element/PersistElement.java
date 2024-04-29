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

import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.util.MessageTracker;

import com.boozallen.aiops.mda.generator.common.PersistMode;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Represents a persist instance.
 */
@JsonPropertyOrder({ "type", "mode" })
public class PersistElement implements Persist {

    protected static MessageTracker messageTracker = MessageTracker.getInstance();

    private String type;

    @JsonInclude(Include.NON_NULL)
    private String mode;

    @JsonInclude(Include.NON_NULL)
    private StepDataCollectionType collectionType;

    @JsonInclude(Include.NON_NULL)
    private StepDataRecordType recordType;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * Sets the persist type of this step.
     * 
     * @param type
     *            type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMode() {
        return mode;
    }

    /**
     * Sets the persist mode of this step.
     * 
     * @param mode
     *            persist mode
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StepDataCollectionType getCollectionType() {
        return collectionType;
    }

    /**
     * Sets the collection type of this persist block.
     * 
     * @param collectionType
     *            collection type
     */
    public void setCollectionType(StepDataCollectionType collectionType) {
        this.collectionType = collectionType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StepDataRecordType getRecordType() {
        return recordType;
    }

    /**
     * Sets the record type of this persist block.
     * 
     * @param recordType
     *            record type
     */
    public void setRecordType(StepDataRecordType recordType) {
        this.recordType = recordType;
    }

    @Override
    public void validate() {
        if (StringUtils.isBlank(getType())) {
            messageTracker.addErrorMessage("A persist element has been specified without a required type!");
        }

        if (StringUtils.isNotBlank(getMode()) && !PersistMode.isValid(getMode())) {
            messageTracker.addErrorMessage("Invalid persist mode has been specified: " + getMode());
        }

        if (getCollectionType() != null) {
            getCollectionType().validate();
        }

        if (getRecordType() != null) {
            getRecordType().validate();
        }
    }

}
