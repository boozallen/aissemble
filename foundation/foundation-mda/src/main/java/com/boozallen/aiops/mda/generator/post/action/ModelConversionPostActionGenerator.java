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
import com.boozallen.aiops.mda.metamodel.element.training.ModelConversionPostAction;

/**
 * Enables the generation of the model conversion scaffolding code if a
 * model-conversion post-action is found in the metamodel of a machine-learning
 * pipeline.
 */
public class ModelConversionPostActionGenerator extends PostActionGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                         | Template                                 | Generated File                             |
     * |--------------------------------|------------------------------------------|--------------------------------------------|
     * | modelConversionPostActionBase  | post-action/model.conversion.base.py.vm  | post_action/${modelConversionFileName}.py  |
     */


    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean shouldGenerateFile(PostAction postAction) {
        return isModelConversionPostAction(postAction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getOutputFileName(String baseOutputFile, PostAction postAction) {
        ModelConversionPostAction modelConversionPostAction = (ModelConversionPostAction) postAction;
        String modelConversionFileName = getGeneratedFileName(modelConversionPostAction);
        return replace("modelConversionFileName", baseOutputFile, modelConversionFileName);
    }

    /**
     * Returns the generated file name for a model-conversion post-action.
     * 
     * @param postAction
     *            the post-action whose generated file name to return
     * @return generated file name
     */
    public static String getGeneratedFileName(ModelConversionPostAction postAction) {
        ModelConversionType modelConversionType = ModelConversionType.getModelConversionType(postAction);

        String prefix = modelConversionType.getValue();
        if (modelConversionType == ModelConversionType.ONNX) {
            prefix = prefix + "_" + postAction.getModelSource();
        }

        return prefix + "_model_conversion_base";
    }

    private boolean isModelConversionPostAction(PostAction postAction) {
        PostActionType postActionType = PostActionType.getPostActionType(postAction);
        return postActionType == PostActionType.MODEL_CONVERSION;
    }

}
