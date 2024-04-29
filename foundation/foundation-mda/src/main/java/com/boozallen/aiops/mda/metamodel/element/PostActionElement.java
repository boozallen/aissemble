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
import org.technologybrewery.fermenter.mda.metamodel.element.MetamodelElement;
import org.technologybrewery.fermenter.mda.util.MessageTracker;

import com.boozallen.aiops.mda.generator.post.action.ModelConversionType;
import com.boozallen.aiops.mda.generator.post.action.PostActionType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Represents a post-action instance.
 */
@JsonPropertyOrder({ "name", "type", "modelTarget", "modelSource" })
public class PostActionElement extends MetamodelElement implements PostAction {

    protected static MessageTracker messageTracker = MessageTracker.getInstance();

    private String type;

    @JsonInclude(Include.NON_NULL)
    private String modelTarget;

    @JsonInclude(Include.NON_NULL)
    private String modelSource;

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
    @Override
    public String getModelTarget() {
        return modelTarget;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getModelSource() {
        return modelSource;
    }

    /**
     * Sets the type of this post-action.
     * 
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Sets the model target of this post-action.
     * 
     * @param modelTarget
     */
    public void setModelTarget(String modelTarget) {
        this.modelTarget = modelTarget;
    }

    /**
     * Sets the model source of this post-action.
     * 
     * @param modelSource
     */
    public void setModelSource(String modelSource) {
        this.modelSource = modelSource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        super.validate();

        if (StringUtils.isNotBlank(getType())) {
            validatePostActionType();
        }

    }

    private void validatePostActionType() {
        PostActionType postActionType = PostActionType.getPostActionType(this);

        if (postActionType == null) {
            messageTracker.addWarningMessage(
                    "Unknown type '" + getType() + "' for post action " + getName() + " - defaulting to freeform type");
            setType(PostActionType.FREEFORM.getValue());
        }

        if (postActionType == PostActionType.MODEL_CONVERSION) {
            if (StringUtils.isBlank(getModelSource()) || StringUtils.isBlank(getModelTarget())) {
                messageTracker.addErrorMessage(
                        "A model-conversion post action has been specified without a modelSource and/or modelTarget!");
            }

            ModelConversionType modelConversionType = ModelConversionType.getModelConversionType(this);
            if (modelConversionType == null) {
                messageTracker.addWarningMessage("Unknown modelTarget '" + getModelTarget() + "' for post action "
                        + getName() + " - default to custom modelTarget");
                setModelTarget(ModelConversionType.CUSTOM.getValue());
            }

        }
    }

}
