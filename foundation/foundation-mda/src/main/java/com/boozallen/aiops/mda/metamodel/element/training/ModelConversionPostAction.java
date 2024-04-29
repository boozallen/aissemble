package com.boozallen.aiops.mda.metamodel.element.training;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.generator.post.action.ModelConversionPostActionGenerator;
import com.boozallen.aiops.mda.generator.post.action.ModelConversionType;
import com.boozallen.aiops.mda.generator.util.PipelineUtils;
import com.boozallen.aiops.mda.metamodel.element.BasePostActionDecorator;
import com.boozallen.aiops.mda.metamodel.element.PostAction;

/**
 * Decorates {@link PostAction} with model-conversion functionality.
 */
public class ModelConversionPostAction extends BasePostActionDecorator {

    /**
     * {@inheritDoc}
     */
    public ModelConversionPostAction(PostAction postActionToDecorate) {
        super(postActionToDecorate);
    }

    /**
     * Returns the file name of the model conversion class.
     * 
     * @return model conversion class name
     */
    public String getModelConversionFileName() {
        return ModelConversionPostActionGenerator.getGeneratedFileName(this);
    }

    /**
     * Returns the name of the model conversion class.
     * 
     * @return model conversion class name
     */
    public String getModelConversionClassName() {
        String fileName = getModelConversionFileName();
        return PipelineUtils.deriveUpperCamelNameFromLowerUnderscoreName(fileName);
    }

    /**
     * Returns the model conversion type.
     * 
     * @return model conversion type
     */
    public ModelConversionType getModelConversionType() {
        return ModelConversionType.getModelConversionType(this);
    }

    /**
     * Whether this model conversion post-action is a custom model conversion.
     * 
     * @return true if this post-action is a custom model conversion
     */
    public boolean isCustomModelConversion() {
        return getModelConversionType() == ModelConversionType.CUSTOM;
    }

    /**
     * Whether this model conversion post-action is an onnx model conversion.
     * 
     * @return true if this post-action is an onnx model conversion
     */
    public boolean isOnnxModelConversion() {
        return getModelConversionType() == ModelConversionType.ONNX;
    }

}
