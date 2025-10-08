package com.boozallen.aiops.mda.generator.post.action;

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
