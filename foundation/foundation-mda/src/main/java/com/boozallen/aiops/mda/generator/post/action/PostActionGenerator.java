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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

import com.boozallen.aiops.mda.generator.AbstractPythonGenerator;
import com.boozallen.aiops.mda.generator.common.PipelineEnum;
import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.generator.util.PipelineUtils;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import com.boozallen.aiops.mda.metamodel.element.PostAction;
import com.boozallen.aiops.mda.metamodel.element.Step;
import com.boozallen.aiops.mda.metamodel.element.python.MachineLearningPipeline;
import com.boozallen.aiops.mda.metamodel.element.training.ModelConversionPostAction;
import com.boozallen.aiops.mda.metamodel.element.training.OnnxModelConversionPostAction;
import com.boozallen.aiops.mda.metamodel.element.util.PythonElementUtils;

/**
 * Enables the generation of scaffolding code for each post-action found in the
 * metamodel of a machine-learning pipeline.
 */
public class PostActionGenerator extends AbstractPythonGenerator {
    /*--~-~-~~
     * Usages:
     * | Target          | Template                            | Generated File                         |
     * |-----------------|-------------------------------------|----------------------------------------|
     * | postActionBase  | post-action/post.action.base.py.vm  | post_action/${postActionName}_base.py  |
     * | postActionImpl  | post-action/post.action.impl.py.vm  | post_action/${postActionName}.py       |
     */


    /**
     * {@inheritDoc}
     */
    @Override
    public void generate(GenerationContext context) {
        Pipeline targetPipeline = PipelineUtils.getTargetedPipeline(context, metadataContext);

        if (hasPostActions(targetPipeline)) {
            MachineLearningPipeline mlPipeline = new MachineLearningPipeline(targetPipeline);
            Step trainingStep = mlPipeline.getTrainingStep();
            String baseOutputFile = context.getOutputFile();

            for (PostAction postAction : trainingStep.getPostActions()) {
                if (shouldGenerateFile(postAction)) {
                    VelocityContext vc = getNewVelocityContext(context);
    
                    PostAction postActionDecorator = getPostActionDecorator(postAction);
                    vc.put(VelocityProperty.POST_ACTION, postActionDecorator);
    
                    String outputFileName = getOutputFileName(baseOutputFile, postActionDecorator);
                    context.setOutputFile(outputFileName);
                    generateFile(context, vc);
                }
            }
        }
    }

    /**
     * Whether the file should be generated for a post-action.
     * 
     * @param postAction
     *            the post-action to generate the file for
     * @return true if the file should be generated for the post-action
     */
    protected boolean shouldGenerateFile(PostAction postAction) {
        return true;
    }

    /**
     * Returns the output file name for the post-action file.
     * 
     * @param context
     *            generation context
     * @param postAction
     *            post-action whose output file name to return
     * @return
     */
    protected String getOutputFileName(String baseOutputFile, PostAction postAction) {
        String snakeCaseName = PythonElementUtils.getSnakeCaseValue(postAction.getName());
        return replace("postActionName", baseOutputFile, snakeCaseName);
    }

    private boolean hasPostActions(Pipeline pipeline) {
        boolean hasPostActions = false;

        // check if the pipeline is an ML pipeline with a training step that has post-actions
        String pipelineType = pipeline.getType().getName();
        if (PipelineEnum.MACHINE_LEARNING.equalsIgnoreCase(pipelineType)) {
            MachineLearningPipeline mlPipeline = new MachineLearningPipeline(pipeline);
            Step trainingStep = mlPipeline.getTrainingStep();
            hasPostActions = trainingStep != null && CollectionUtils.isNotEmpty(trainingStep.getPostActions());
        }

        return hasPostActions;
    }

    private PostAction getPostActionDecorator(PostAction postAction) {
        PostAction postActionDecorator;

        PostActionType postActionType = PostActionType.getPostActionType(postAction);
        switch (postActionType) {
        case MODEL_CONVERSION:
            postActionDecorator = getModelConversionPostActionDecorator(postAction);
            break;
        case FREEFORM:
        default:
            postActionDecorator = postAction;
            break;
        }

        return postActionDecorator;
    }

    private PostAction getModelConversionPostActionDecorator(PostAction postAction) {
        PostAction postActionDecorator;

        ModelConversionType modelConversionType = ModelConversionType.getModelConversionType(postAction);
        switch (modelConversionType) {
        case ONNX:
            postActionDecorator = new OnnxModelConversionPostAction(postAction);
            break;
        case CUSTOM:
        default:
            postActionDecorator = new ModelConversionPostAction(postAction);
            break;
        }

        return postActionDecorator;
    }

}
