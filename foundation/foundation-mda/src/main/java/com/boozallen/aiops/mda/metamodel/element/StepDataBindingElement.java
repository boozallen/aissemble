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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Represents a step data binding (e.g., inbound, outbound) instance.
 */
@JsonPropertyOrder({ "type", "channelType", "channelName", "nativeCollectionType", "recordType" })
public class StepDataBindingElement implements StepDataBinding {

    protected static MessageTracker messageTracker = MessageTracker.getInstance();

    private String type;
    
    @JsonInclude(Include.NON_NULL)
    private StepDataCollectionType nativeCollectionType;
    
    @JsonInclude(Include.NON_NULL)
    private StepDataRecordType recordType;
    
    @JsonInclude(Include.NON_NULL)
    private String channelType;
    
    @JsonInclude(Include.NON_NULL)
    private String channelName;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @JsonInclude(Include.NON_NULL)
    @Override
    public String getChannelType() {
        return channelType;
    }

    /**
     * {@inheritDoc}
     */
    @JsonInclude(Include.NON_NULL)
    @Override
    public String getChannelName() {
        return channelName;
    }

    /**
     * Sets the type of this step data binding.
     * 
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    @JsonInclude(Include.NON_NULL)
    @Override
    public StepDataCollectionType getNativeCollectionType() {
        return nativeCollectionType;
    }

    /**
     * Sets the native collection type of this step data binding.
     * 
     * @param nativeCollectionType
     *            the native collection type
     */
    public void setNativeCollectionType(StepDataCollectionType nativeCollectionType) {
        this.nativeCollectionType = nativeCollectionType;
    }

    /**
     * {@inheritDoc}
     */
    @JsonInclude(Include.NON_NULL)
    @Override
    public StepDataRecordType getRecordType() {
        return recordType;
    }

    /**
     * Sets the record type of this step data binding.
     * 
     * @param recordType
     *            the record type
     */
    public void setRecordType(StepDataRecordType recordType) {
        this.recordType = recordType;
    }

    /**
     * Sets the channel type of this step data binding.
     * 
     * @param channelType
     *            the channel type
     */
    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    /**
     * Sets the channel name of this step data binding.
     * 
     * @param channelName the name of the channel
     */
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public void validate() {
        if (StringUtils.isBlank(getType())) {
            messageTracker.addErrorMessage("A data binding has been specified without a required type!");
        }

        if (getNativeCollectionType() != null) {
            getNativeCollectionType().validate();
        }

        if (getRecordType() != null) {
            getRecordType().validate();
        }

        if ("messaging".equalsIgnoreCase(getType()) && StringUtils.isEmpty(getChannelName())) {
            messageTracker.addErrorMessage("A channel name is required when using type messaging.");
        }
    }

}
