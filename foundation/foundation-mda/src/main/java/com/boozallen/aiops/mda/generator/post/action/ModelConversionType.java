package com.boozallen.aiops.mda.generator.post.action;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.metamodel.element.PostAction;

/**
 * Enum to represent a model-conversion type.
 */
public enum ModelConversionType {

    ONNX("onnx"),
    CUSTOM("custom")
    ;

    private String value;

    private ModelConversionType(String value) {
        this.value = value;
    }

    /**
     * Returns the value of this model-conversion type.
     * 
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * Returns the model conversion type for a post-action.
     * 
     * @param postAction
     *            the post-action whose model conversion type to return
     * @return model conversion type
     */
    public static ModelConversionType getModelConversionType(PostAction postAction) {
        ModelConversionType found = null;

        for (ModelConversionType modelConversionType : values()) {
            if (modelConversionType.value.equals(postAction.getModelTarget())) {
                found = modelConversionType;
                break;
            }
        }

        return found;
    }

}
