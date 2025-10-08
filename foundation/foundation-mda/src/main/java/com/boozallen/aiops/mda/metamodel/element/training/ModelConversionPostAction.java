package com.boozallen.aiops.mda.metamodel.element.training;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
